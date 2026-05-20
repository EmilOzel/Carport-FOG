package app.controllers;

import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/mine-ordrer", ctx -> getOrdersByUser(ctx, connectionPool));
        app.get("/ordre/{ordreId}", ctx -> getOrderLines(ctx, connectionPool));
        app.post("/bestil-tilbud", ctx -> createOrder(ctx, connectionPool));
    }
    private static void createOrder(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login-side");
            return;
        }
        try {
            int ordreId = OrderMapper.createOrder(userId, connectionPool);
            ctx.redirect("/tegning/" + ordreId);
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }
    private static void getOrdersByUser(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login-side");
            return;
        }
        try {
            List<Object[]> orders = OrderMapper.getOrdersByUser(userId, connectionPool);
            ctx.attribute("orders", orders);
            ctx.render("mine-ordrer.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }

    private static void getOrderLines(Context ctx, ConnectionPool connectionPool) {
        Integer userId = ctx.sessionAttribute("userId");
        if (userId == null) {
            ctx.redirect("/login-side");
            return;
        }
        try {
            int ordreId = Integer.parseInt(ctx.pathParam("ordreId"));
            List<Object[]> lines = OrderMapper.getOrderLines(ordreId, connectionPool);
            ctx.attribute("lines", lines);
            ctx.attribute("ordreId", ordreId);
            ctx.render("ordre-detaljer.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("error.html");
        }
    }

}