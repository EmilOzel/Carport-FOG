package app.controllers;

import app.dto.CarportForm;
import app.entities.Carport;
import app.entities.Order;
import app.services.CarportService;
import app.services.PriceCalculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDateTime;

public class CarportController {

    public static void addRoutes(Javalin app) {
        app.get("/byg-carport", CarportController::showBuildPage);
        app.get("/vælg-mål", CarportController::showMeasurementPage);
        app.get("/v%C3%A6lg-m%C3%A5l", CarportController::showMeasurementPage);
        app.post("/carport/order", CarportController::createOrder);
    }

    private static void showBuildPage(Context ctx) {
        ctx.render("byg-carport.html");
    }

    private static void showMeasurementPage(Context ctx) {
        ctx.render("vælg-mål.html");
    }

    private static void createOrder(Context ctx) {
        try {
            Carport carport = createCarportFromRequest(ctx);

            PriceCalculator priceCalculator = new PriceCalculator();
            double basePrice = priceCalculator.calculateBasePrice(carport);

            Order order = new Order(0, 0, 0, "Afventer tilbud", LocalDateTime.now(), basePrice);
            order.setBasePrice(basePrice);
            order.setDiscountPercent(0);
            order.setDiscountAmount(0);
            order.setTotalPrice(basePrice);

            ctx.sessionAttribute("currentCarport", carport);
            ctx.sessionAttribute("currentOrder", order);
            ctx.redirect("/tegning");
        } catch (IllegalArgumentException e) {
            ctx.attribute("error", e.getMessage());
            ctx.render("vælg-mål.html");
        }
    }

    private static Carport createCarportFromRequest(Context ctx) {
        CarportForm form = readCarportForm(ctx);
        CarportService carportService = new CarportService();
        return carportService.createCarportFromForm(form);
    }

    private static CarportForm readCarportForm(Context ctx) {
        int width = parseRequiredInt(ctx, "bredde");
        int length = parseRequiredInt(ctx, "længde");
        int height = parseRequiredInt(ctx, "højde");
        String roofType = ctx.formParam("tagtype");
        if (roofType == null || roofType.isBlank()) {
            roofType = "FLAT";
        }
        boolean hasShed = "yes".equals(ctx.formParam("redskabsrum"));
        int shedWidth = parseOptionalInt(ctx.formParam("skurBredde"));
        int shedLength = parseOptionalInt(ctx.formParam("skurLængde"));

        return new CarportForm(width, length, height, roofType, hasShed, shedWidth, shedLength);
    }

    private static int parseRequiredInt(Context ctx, String paramName) {
        String value = ctx.formParam(paramName);

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Vælg en værdi for " + paramName);
        }

        return Integer.parseInt(value);
    }

    private static int parseOptionalInt(String value) {
        if (value == null || value.isBlank()) {
            return 0;
        }

        return Integer.parseInt(value);
    }
}
