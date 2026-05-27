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
        app.get("/login",             ctx -> ctx.render("login.html"));
        app.get("/opret-bruger",      ctx -> ctx.render("register.html"));
        app.get("/opret-bruger-side", ctx -> ctx.render("register.html"));
        app.get("/rediger-profil", ctx-> editProfilePage(ctx));
        app.get("/bruger",            ctx -> userPage(ctx, connectionPool));
        app.get("/bruger-side",       ctx -> userPage(ctx, connectionPool));
        app.post("/rediger-profil", ctx-> updateProfile(ctx,connectionPool));
        app.post("/login",            ctx -> login(ctx, connectionPool));
        app.post("/opret-bruger",     ctx -> createUser(ctx, connectionPool));
        app.get("/logout",            ctx -> logout(ctx));
    }


    private static void userPage(Context ctx, ConnectionPool connectionPool) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login");
            return;
        }
        String flashError = ctx.sessionAttribute("flashError");
        if (flashError != null) {
            ctx.sessionAttribute("flashError", null);
            ctx.attribute("error", flashError);
        }
        try {
            List<Object[]> orders = OrderMapper.getOrdersByUser(user.getId(), connectionPool);
            ctx.attribute("orders", orders);
        } catch (DatabaseException e) {
            ctx.attribute("orders", new ArrayList<>());
            ctx.attribute("error", e.getMessage());
        }
        ctx.attribute("user", user);
        ctx.render("user-profile.html");
    }

    private static void login(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        try {
            User user = UserMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.sessionAttribute("userId", user.getId());

            String redirectUrl = ctx.sessionAttribute("redirectAfterLogin");
            if (redirectUrl != null) {
                ctx.sessionAttribute("redirectAfterLogin", null);
                ctx.redirect(redirectUrl);
            } else {

                ctx.redirect("/bruger-side");
            }

        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("login.html");
        }
    }

    private static void createUser(Context ctx, ConnectionPool connectionPool) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String firstName = ctx.formParam("fornavn");
        String lastName = ctx.formParam("efternavn");
        String address = ctx.formParam("adresse");
        String phone = ctx.formParam("telefon");
        String zip = ctx.formParam("postnummer");
        try {
            UserMapper.createUser(email, password, firstName, lastName, address, phone, zip, connectionPool);
            ctx.redirect("/login");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("register.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/");
    }


    private static void editProfilePage(Context ctx) {

        User user = ctx.sessionAttribute("currentUser");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        ctx.attribute("user", user);

        ctx.render("userP-rediger.html");
    }


    private static void updateProfile(Context ctx,
                                      ConnectionPool connectionPool) {

        User user = ctx.sessionAttribute("currentUser");

        if (user == null) {
            ctx.redirect("/login");
            return;
        }

        String firstName = ctx.formParam("fornavn");
        String lastName = ctx.formParam("efternavn");
        String email = ctx.formParam("email");
        String address = ctx.formParam("adresse");
        String zip = ctx.formParam("postnummer");

        try {

            UserMapper.updateUser(
                    user.getId(),
                    firstName,
                    lastName,
                    email,
                    address,
                    zip,
                    connectionPool
            );

            // opdater session user
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setAddress(address);
            user.setZipcode(Integer.parseInt(zip));

            ctx.sessionAttribute("currentUser", user);

            ctx.attribute("success", "Profil opdateret");
            ctx.attribute("user", user);

            ctx.render("userP-rediger.html");

        } catch (DatabaseException e) {

            if (e.getMessage().toLowerCase().contains("zip")
                    || e.getMessage().toLowerCase().contains("post")) {

                ctx.attribute("error",
                        "Postnummeret findes ikke");

            } else {

                ctx.attribute("error",
                        "Postnummeret findes ikke");
            }

            ctx.attribute("user", user);

            ctx.render("userP-rediger.html");
        }
    }



}