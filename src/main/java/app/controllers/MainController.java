package app.controllers;

import app.Main;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
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
        app.get("/login-side", MainController::loginPage);
        app.get("/opret-bruger-side", MainController::createUserPage);
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

    private static void createUserPage(Context ctx) {
        ctx.render("opret-bruger.html");
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