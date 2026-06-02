package app.controllers;

import app.entities.Carport;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalespersonController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/saelger",                            ctx -> salespersonDashboard(ctx, connectionPool));
        app.get("/saelger/ordre/{id}",                 ctx -> orderDetail(ctx, connectionPool));
        app.post("/saelger/ordre/{id}/send-tilbud",    ctx -> sendOffer(ctx, connectionPool));
        app.post("/saelger/ordre/{id}/annuller",       ctx -> cancelOrder(ctx, connectionPool));
    }

    private static boolean isSalesperson(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null && ("salesperson".equals(user.getRole()) || "admin".equals(user.getRole()));
    }

    private static void salespersonDashboard(Context ctx, ConnectionPool connectionPool) {
        if (!isSalesperson(ctx)) {
            ctx.redirect("/login");
            return;
        }
        try {
            List<Object[]> orders = AdminMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.attribute("user", ctx.sessionAttribute("currentUser"));
            ctx.render("salesperson.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("salesperson.html");
        }
    }

    private static void orderDetail(Context ctx, ConnectionPool connectionPool) {
        if (!isSalesperson(ctx)) {
            ctx.redirect("/login");
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            Object[] order = AdminMapper.getOrderDetail(orderId, connectionPool);
            Carport carport = OrderMapper.getCarportByOrder(orderId, connectionPool);
            String svg = carport == null ? null : new CarportSvg(carport).toString();

            ctx.attribute("order", order);
            ctx.attribute("svg", svg);
            ctx.attribute("user", ctx.sessionAttribute("currentUser"));
            ctx.render("salesperson-order.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/saelger");
        }
    }

    private static void cancelOrder(Context ctx, ConnectionPool connectionPool) {
        if (!isSalesperson(ctx)) { ctx.status(403); return; }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            AdminMapper.deleteOrder(orderId, connectionPool);
            ctx.redirect("/saelger");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/saelger");
        }
    }

    private static void sendOffer(Context ctx, ConnectionPool connectionPool) {
        if (!isSalesperson(ctx)) {
            ctx.status(403);
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            String priceStr = ctx.formParam("pris");
            double price = Double.parseDouble(priceStr.replace(",", "."));
            OrderMapper.sendOffer(orderId, price, connectionPool);
            ctx.redirect("/saelger");
        } catch (NumberFormatException e) {
            ctx.attribute("error", "Ugyldig pris");
            ctx.redirect("/saelger");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/saelger");
        }
    }
}
