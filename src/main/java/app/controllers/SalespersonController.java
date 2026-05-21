package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class SalespersonController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/saelger",                        ctx -> salespersonDashboard(ctx, connectionPool));
        app.post("/saelger/ordre/{id}/godkend",    ctx -> approveOrder(ctx, connectionPool));
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
            ctx.render("salesperson.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("salesperson.html");
        }
    }

    private static void approveOrder(Context ctx, ConnectionPool connectionPool) {
        if (!isSalesperson(ctx)) {
            ctx.status(403);
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            String priceStr = ctx.formParam("pris");
            double price = Double.parseDouble(priceStr.replace(",", "."));
            OrderMapper.approveOrder(orderId, price, connectionPool);
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
