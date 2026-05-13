package app.controllers;

import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.UserMapper;
import io.javalin.Javalin;

public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {

        app.get("/login", ctx -> {
            String success = ctx.sessionAttribute("successMessage");
            if (success != null) {
                ctx.attribute("successMessage", success);
                ctx.sessionAttribute("successMessage", null);
            }
            ctx.render("login-side.html");
        });

        app.post("/login", ctx -> {
            try {
                User user = UserMapper.login(
                        ctx.formParam("email"),
                        ctx.formParam("password"),
                        connectionPool
                );

                ctx.sessionAttribute("currentUser", user);
                ctx.redirect("/build");

            } catch (DatabaseException e) {
                ctx.attribute("error", e.getMessage());
                ctx.render("login.html");
            }
        });

        app.get("/logout", ctx -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

        app.get("/register", ctx -> ctx.render("register.html"));

        app.post("/register", ctx -> {
            try {
                UserMapper.createUser(
                        ctx.formParam("email"),
                        ctx.formParam("password"),
                        connectionPool
                );
                ctx.sessionAttribute("successMessage", "Konto oprettet! Du kan nu logge ind.");
                ctx.redirect("/login");

            } catch (DatabaseException e) {
                ctx.attribute("error", e.getMessage());
                ctx.render("register.html");
            }
        });
    }
}
