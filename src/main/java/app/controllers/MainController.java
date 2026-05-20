package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class MainController {
    public static void addRoutes(Javalin app) {
        app.get("/", MainController::index);
        app.get("/færdige-modeller", MainController::finishedModels);
        app.get("/login-side", MainController::loginPage);
        app.get("/opret-bruger", MainController::createUserPage);
    }

    private static void index(Context ctx) {
        ctx.render("index.html");
    }

    private static void finishedModels(Context ctx) {
        ctx.render("færdige-modeller.html");
    }

    private static void loginPage(Context ctx) {
        ctx.render("login-side.html");
    }

    private static void createUserPage(Context ctx) {
        ctx.render("opret-bruger.html");
    }
}
