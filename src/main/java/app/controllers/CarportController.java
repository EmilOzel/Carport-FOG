package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class CarportController {
    public static void addRoutes(Javalin app) {
        app.get("/carporte/enkelt", CarportController::singleCarports);
        app.get("/carporte/quick-byg", CarportController::quickBuild);
    }

    private static void singleCarports(Context ctx) {
        ctx.render("single-carports.html");
    }

    private static void quickBuild(Context ctx) {
        ctx.render("quick-build.html");
    }
}