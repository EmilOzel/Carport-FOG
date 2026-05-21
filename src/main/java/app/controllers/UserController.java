package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.OrderMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.List;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/bruger", ctx -> userPage(ctx, connectionPool));
        app.get("/bruger-side", ctx -> userPage(ctx, connectionPool));
        app.post("/login", ctx -> login(ctx, connectionPool));
        app.post("/opret-bruger", ctx -> createUser(ctx, connectionPool));
        app.get("/logout", ctx -> logout(ctx));
    }

    private static void userPage(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login-side");
            return;
        }
        try {
            List<Object[]> orders = OrderMapper.getOrdersByUser(user.getId(), connectionPool);
            ctx.attribute("orders", orders);
        } catch (DatabaseException e) {
            ctx.attribute("orders", new ArrayList<>());
        }
        ctx.attribute("user", user);
        ctx.render("bruger-side.html");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.sessionAttribute("userId", user.getId());
            ctx.redirect("/bruger-side");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("login-side.html");
        }
    }

    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String firstName = ctx.formParam("fornavn");
        String lastName = ctx.formParam("efternavn");
        String address = ctx.formParam("adresse");
        String zip = ctx.formParam("postnummer");
        try {
            UserMapper.createUser(email, password, firstName, lastName, address, zip, connectionPool);
            ctx.redirect("/login-side");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("opret-bruger.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }
}