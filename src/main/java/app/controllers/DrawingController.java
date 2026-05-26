package app.controllers;

import app.entities.Carport;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DrawingController {

    public static void addRoutes(Javalin app) {
        app.get("/tegning", DrawingController::showDrawing);
    }

    private static void showDrawing(Context ctx) {
        Carport carport = ctx.sessionAttribute("currentCarport");

        if (carport == null) {
            ctx.redirect("/choose-dimensions");
            return;
        }

        ctx.attribute("carport", carport);
        ctx.render("drawing.html");
    }
}
