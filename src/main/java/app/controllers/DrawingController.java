package app.controllers;

import app.entities.Carport;
import app.services.CarportSvg;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class DrawingController {

    public static void addRoutes(Javalin app) {
        app.get("/tegning", DrawingController::showDrawing);
    }

    private static void showDrawing(Context ctx) {
        Carport carport = ctx.sessionAttribute("currentCarport");

        if (carport == null) {
            ctx.redirect("/vælg-mål");
            return;
        }

        String svg = new CarportSvg(carport).toString();

        ctx.attribute("carport", carport);
        ctx.attribute("svg", svg);
        ctx.render("tegning.html");
    }
}
