package app.controllers;

import app.Main;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import app.persistence.OrderMapper;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    public static void addRoutes(Javalin app) {
        app.get("/", MainController::index);
        app.get("/faerdige-modeller", MainController::finishedModels);
        app.get("/færdige-modeller", MainController::finishedModels);
        app.get("/byg-carport", MainController::buildCarport);
        app.get("/vaelg-maal", MainController::chooseMeasurements);
        app.get("/vælg-mål", MainController::chooseMeasurements);
        app.get("/login", MainController::loginPage);
        app.get("/login-side", MainController::loginPage);
        app.post("/login", MainController::login);
        app.get("/opret-bruger", MainController::createUserPage);
        app.get("/opret-bruger-side", MainController::createUserPage);
        app.post("/opret-bruger", MainController::createUser);
        app.get("/logout", MainController::logout);
        app.get("/bruger", MainController::userPage);
        app.get("/bruger-side", MainController::userPage);
    }

    private static void index(Context ctx) {
        ctx.render("index.html");
    }

    private static void finishedModels(Context ctx) {
        ctx.render("færdige-modeller.html");
    }

    private static void buildCarport(Context ctx) {
        ctx.render("byg-carport.html");
    }

    private static void chooseMeasurements(Context ctx) {
        ctx.render("vælg-mål.html");
    }

    private static void loginPage(Context ctx) {
        ctx.render("login-side.html");
    }

    private static void login(Context ctx) {
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(email, password, Main.connectionPool);
            ctx.sessionAttribute("currentUser", user);
            ctx.sessionAttribute("userId",user.getId());
            ctx.redirect("/bruger-side");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("login-side.html");
        }
    }

    private static void createUserPage(Context ctx) {
        ctx.render("opret-bruger.html");
    }

    private static void createUser(Context ctx) {
        String fornavn = ctx.formParam("fornavn");
        String efternavn = ctx.formParam("efternavn");
        String email = ctx.formParam("email");
        String password = ctx.formParam("password");
        String adresse = ctx.formParam("adresse");
        String postnummer = ctx.formParam("postnummer");

        try {
            UserMapper.createUser(email, password, fornavn, efternavn, adresse, postnummer, Main.connectionPool);
            ctx.redirect("/login-side");
        } catch (DatabaseException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("opret-bruger.html");
        }
    }

    private static void logout(Context ctx) {
        ctx.sessionAttribute("currentUser", null);
        ctx.redirect("/");
    }

    private static void userPage(Context ctx) {
        User user = ctx.sessionAttribute("currentUser");
        if (user == null) {
            ctx.redirect("/login-side");
            return;
        }
        ctx.attribute("user", user);
        try {
            List<Object[]> orders = OrderMapper.getOrdersByUser(user.getId(), Main.connectionPool);
            ctx.attribute("orders", orders);
        } catch (DatabaseException e) {
            ctx.attribute("orders", new ArrayList<>());
        }
        ctx.render("bruger-side.html");
    }
}
