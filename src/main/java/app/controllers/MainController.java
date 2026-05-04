package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class MainController {
    public static void addRoutes(Javalin app) {
        app.get("/", MainController::index);
    }

    private static void index(Context ctx) {
        ctx.render("index.html");
    }
}