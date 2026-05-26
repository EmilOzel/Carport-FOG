package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;
import java.util.Map;

public class AdminController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/admin",                         ctx -> adminDashboard(ctx, connectionPool));
        app.get("/admin/brugere",                 ctx -> adminUsers(ctx, connectionPool));
        app.get("/admin/statistik",               ctx -> adminStatistics(ctx, connectionPool));
        app.get("/admin/opret-saelger",           ctx -> showCreateSalesperson(ctx));
        app.post("/admin/opret-saelger",          ctx -> createSalesperson(ctx, connectionPool));
        app.post("/admin/ordre/{id}/status",      ctx -> updateOrderStatus(ctx, connectionPool));
        app.post("/admin/bruger/{id}/rolle",      ctx -> updateUserRole(ctx, connectionPool));
        app.post("/admin/bruger/{id}/slet",       ctx -> deleteUser(ctx, connectionPool));
    }

    private static boolean isAdmin(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        return user != null && "admin".equals(user.getRole());
    }

    private static void adminDashboard(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.redirect("/login");
            return;
        }
        try {
            List<Object[]> orders = AdminMapper.getAllOrders(connectionPool);
            ctx.attribute("orders", orders);
            ctx.attribute("user", ctx.sessionAttribute("currentUser"));
            ctx.render("admin-orders.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("admin-orders.html");
        }
    }

    private static void adminUsers(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.redirect("/login");
            return;
        }
        try {
            List<User> users = AdminMapper.getAllUsers(connectionPool);
            ctx.attribute("users", users);
            ctx.attribute("user", ctx.sessionAttribute("currentUser"));
            ctx.render("admin-users.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("admin-users.html");
        }
    }

    private static void updateOrderStatus(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.status(403);
            return;
        }
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));
            String newStatus = ctx.formParam("status");
            AdminMapper.updateOrderStatus(orderId, newStatus, connectionPool);
            ctx.redirect("/admin");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/admin");
        }
    }

    private static void deleteUser(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.status(403);
            return;
        }
        try {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            AdminMapper.deleteUser(userId, connectionPool);
            ctx.redirect("/admin/brugere");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/admin/brugere");
        }
    }

    private static void adminStatistics(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.redirect("/login");
            return;
        }
        try {
            ctx.attribute("stats", AdminMapper.getStatistics(connectionPool));
            ctx.attribute("user", ctx.sessionAttribute("currentUser"));
            ctx.render("admin-statistics.html");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("admin-statistics.html");
        }
    }

    private static void showCreateSalesperson(Context ctx) {
        if (!isAdmin(ctx)) {
            ctx.redirect("/login");
            return;
        }
        ctx.render("admin-create-salesperson.html");
    }

    private static void createSalesperson(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.redirect("/login");
            return;
        }
        String email      = ctx.formParam("email");
        String firstName  = ctx.formParam("fornavn");
        String password   = ctx.formParam("adgangskode");
        try {
            AdminMapper.createSalesperson(email, password, firstName, connectionPool);
            ctx.redirect("/admin/brugere");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("admin-create-salesperson.html");
        }
    }

    private static void updateUserRole(Context ctx, ConnectionPool connectionPool) {
        if (!isAdmin(ctx)) {
            ctx.status(403);
            return;
        }
        try {
            int userId = Integer.parseInt(ctx.pathParam("id"));
            String newRole = ctx.formParam("rolle");
            User target = AdminMapper.getUserById(userId, connectionPool);
            if ("customer".equals(target.getRole())) {
                ctx.redirect("/admin/brugere");
                return;
            }
            AdminMapper.updateUserRole(userId, newRole, connectionPool);
            ctx.redirect("/admin/brugere");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.redirect("/admin/brugere");
        }
    }
}
