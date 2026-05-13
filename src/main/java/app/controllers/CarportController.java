package app.controllers;

import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class CarportController {
    public static void addRoutes(Javalin app, ConnectionPool connectionPool) {
        app.get("/carporte/enkelt", CarportController::singleCarports);
        app.get("/carporte/quick-byg", CarportController::quickBuild);
    }

    private static void singleCarports(Context ctx) {
        ctx.render("færdige-modeller.html");
    }

    private static void quickBuild(Context ctx) {
        ctx.render("byg-carport.html");
    }
}
