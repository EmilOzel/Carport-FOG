package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class CarportController {
    public static void addRoutes(Javalin app) {
        app.get("/carporte/opret-bruger", CarportController::registerUser);
        app.get("/carporte/login-side", CarportController::loginPage);
        app.get("/carporte/vælg-mål", CarportController::measurement);
        app.get("/carporte/bruger-side", CarportController::userPage);
        app.get("/carporte/færdige-modeller", CarportController::completedModels);
        app.get("/carporte/byg-carport", CarportController::buildShed);
    }

    private static void registerUser(Context ctx) {
        ctx.render("opret-bruger.html");
    }
    private static void loginPage(Context ctx) {
        ctx.render("login-side.html");
    }
    private static void measurement(Context ctx) {
        ctx.render("vælg-mål.html");
    }
    private static void userPage(Context ctx) {
        ctx.render("bruger-side.html");
    }
    private static void completedModels(Context ctx) {
        ctx.render("færdige-modeller.html");
    }
    private static void buildShed(Context ctx) {
        ctx.render("byg-carport.html");
    }

}


