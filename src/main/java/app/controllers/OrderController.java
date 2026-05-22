package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

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
            List<Object[]> lines = OrderMapper.getOrderLines(ordreId, connectionPool);
            Object[] order = AdminMapper.getOrderDetail(ordreId, connectionPool);
            ctx.attribute("lines", lines);
            ctx.attribute("ordreId", ordreId);
            ctx.attribute("order", order);
            ctx.render("order-details.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }

}