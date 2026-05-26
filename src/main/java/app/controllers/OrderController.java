package app.controllers;

import app.entities.Carport;
import app.entities.MaterialLine;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import app.services.StyklisteCalculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {
    private static final String MATERIALS_SESSION_PREFIX = "materialsForOrder_";

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/mine-ordrer",           ctx -> getOrdersByUser(ctx, connectionPool));
        app.get("/ordre/{ordreId}",       ctx -> getOrderLines(ctx, connectionPool));
        app.post("/bestil-tilbud",        ctx -> createOrder(ctx, connectionPool));
        app.get("/ordre/{id}/betal",       ctx -> showPayment(ctx, connectionPool));
        app.post("/ordre/{id}/betal",      ctx -> payOrder(ctx, connectionPool));
        app.post("/ordre/{id}/accepter",   ctx -> acceptOffer(ctx, connectionPool));
        app.post("/ordre/{id}/afvis",      ctx -> rejectOffer(ctx, connectionPool));
    }
    private static void createOrder(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            int ordreId = OrderMapper.createOrder(userId, connectionPool);
            saveCarportAndMaterialList(ctx, ordreId, connectionPool);
            ctx.redirect("/ordre/" + ordreId);
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }
    private static void getOrdersByUser(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            List<Object[]> orders = OrderMapper.getOrdersByUser(userId, connectionPool);
            ctx.attribute("orders", orders);
            ctx.attribute("user", AdminMapper.getUserById(userId, connectionPool));
            ctx.render("my-orders.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void showPayment(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) { ctx.redirect("/login"); return; }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Object[] order = AdminMapper.getOrderDetail(orderId, connectionPool);
            if (!"approved".equals(order[5])) {
                ctx.redirect("/bruger-side");
                return;
            }
            ctx.attribute("order", order);
            ctx.render("payment.html");
        } catch (DatabaseException e) {
            ctx.redirect("/bruger-side");
        }
    }

    private static void payOrder(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            OrderMapper.payOrder(orderId, connectionPool);
            ctx.redirect("/bruger-side");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/bruger-side");
        }
    }

    private static void acceptOffer(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) { ctx.redirect("/login"); return; }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            OrderMapper.acceptOffer(orderId, connectionPool);
            ctx.redirect("/bruger-side");
        } catch (DatabaseException e) {
            ctx.redirect("/bruger-side");
        }
    }

    private static void rejectOffer(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) { ctx.redirect("/login"); return; }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            OrderMapper.rejectOffer(orderId, connectionPool);
            ctx.redirect("/bruger-side");
        } catch (DatabaseException e) {
            ctx.redirect("/bruger-side");
        }
    }

    private static void getOrderLines(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login");
            return;
        }
        try {
            int ordreId = Integer.parseInt(ctx.pathParam("ordreId"));
            Object[] order = AdminMapper.getOrderDetail(ordreId, connectionPool);
            boolean isCompleted = "completed".equals(order[5]);
            List<Object[]> lines = List.of();
            List<MaterialLine> materials = List.of();
            String svg = null;

            if (isCompleted) {
                lines = OrderMapper.getOrderLines(ordreId, connectionPool);
                materials = createMaterialList(ctx, ordreId, lines, connectionPool);
                saveMissingCarportData(ctx, ordreId, lines, materials, connectionPool);
                lines = OrderMapper.getOrderLines(ordreId, connectionPool);
                updateDisplayedPrice(order, materials);
                svg = createDrawing(ordreId, connectionPool);
            }

            ctx.attribute("lines", lines);
            ctx.attribute("materials", materials);
            ctx.attribute("isCompleted", isCompleted);
            ctx.attribute("svg", svg);
            ctx.attribute("ordreId", ordreId);
            ctx.attribute("order", order);
            ctx.render("order-details.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void saveCarportAndMaterialList(Context ctx, int ordreId, ConnectionPool connectionPool) throws DatabaseException {
        Carport carport = ctx.sessionAttribute("currentCarport");

        if (carport == null) {
            return;
        }

        StyklisteCalculator calculator = new StyklisteCalculator();
        List<MaterialLine> materials = calculator.calculateMaterialList(carport);

        OrderMapper.saveCarportForOrder(ordreId, carport, materials, connectionPool);
        ctx.sessionAttribute(materialSessionKey(ordreId), materials);
    }

    private static List<MaterialLine> createMaterialList(Context ctx, int ordreId, List<Object[]> lines, ConnectionPool connectionPool) throws DatabaseException {
        if (!lines.isEmpty()) {
            return List.of();
        }

        List<MaterialLine> materials = ctx.sessionAttribute(materialSessionKey(ordreId));

        if (materials != null) {
            return materials;
        }

        Carport carport = ctx.sessionAttribute("currentCarport");

        if (carport == null) {
            carport = OrderMapper.getCarportByOrder(ordreId, connectionPool);
        }

        if (carport == null) {
            return List.of();
        }

        StyklisteCalculator calculator = new StyklisteCalculator();
        return calculator.calculateMaterialList(carport);
    }

    private static void saveMissingCarportData(Context ctx, int ordreId, List<Object[]> lines, List<MaterialLine> materials, ConnectionPool connectionPool) throws DatabaseException {
        if (!lines.isEmpty() || materials.isEmpty()) {
            return;
        }

        Carport carport = ctx.sessionAttribute("currentCarport");

        if (carport == null || OrderMapper.getCarportByOrder(ordreId, connectionPool) != null) {
            return;
        }

        OrderMapper.saveCarportForOrder(ordreId, carport, materials, connectionPool);
    }

    private static void updateDisplayedPrice(Object[] order, List<MaterialLine> materials) {
        if ((double) order[6] > 0 || materials.isEmpty()) {
            return;
        }

        double totalPrice = 0;

        for (MaterialLine material : materials) {
            totalPrice += material.getTotalPrice();
        }

        order[6] = totalPrice;
    }

    private static String createDrawing(int ordreId, ConnectionPool connectionPool) throws DatabaseException {
        Carport carport = OrderMapper.getCarportByOrder(ordreId, connectionPool);

        if (carport == null) {
            return null;
        }

        return new CarportSvg(carport).toString();
    }

    private static String materialSessionKey(int ordreId) {
        return MATERIALS_SESSION_PREFIX + ordreId;
    }

}
