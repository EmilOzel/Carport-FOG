package app.controllers;

import io.javalin.Javalin;
import io.javalin.http.Context;

public class CarportController {
    public static void addRoutes(Javalin app) {
        app.get("/", CarportController::index);
        app.get("/faerdige-modeller", CarportController::finishedModels);
        app.get("/færdige-modeller", CarportController::finishedModels);
        app.get("/byg-carport", CarportController::buildCarport);
        app.get("/vaelg-maal", CarportController::chooseMeasurements);
        app.get("/vælg-mål", CarportController::chooseMeasurements);
        app.get("/carporte/enkelt", CarportController::finishedModels);
        app.get("/carporte/quick-byg", CarportController::buildCarport);
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
}
