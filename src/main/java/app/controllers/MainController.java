package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class MainController {

    public static void addRoutes(Javalin app) {
        app.get("/", MainController::index);
        app.get("/færdige-modeller", MainController::finishedModels);
        app.get("/f%C3%A6rdige-modeller", MainController::finishedModels);
        app.get("/login-side", MainController::login);
        app.get("/opret-bruger-side", MainController::createUser);
        app.get("/bruger-side", MainController::userPage);
    }

    private static void index(Context ctx) {
        ctx.render("index.html");
    }

    private static void finishedModels(Context ctx) {
        ctx.render("færdige-modeller.html");
    }

    private static void login(Context ctx) {
        ctx.render("login-side.html");
    }

    private static void createUser(Context ctx) {
        ctx.render("opret-bruger.html");
    }

    private static void userPage(Context ctx) {
        ctx.render("bruger-side.html");
    }
}
