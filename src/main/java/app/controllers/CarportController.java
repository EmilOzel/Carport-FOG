package app.controllers;

import app.dto.CarportForm;
import app.entities.Carport;
import app.entities.Order;
import app.entities.User;
import app.services.CarportDimensions;
import app.services.CarportService;
import app.services.PriceCalculator;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.time.LocalDateTime;

public class CarportController {

    public static void addRoutes(Javalin app) {
        app.get("/", ctx -> ctx.render("index.html"));

        app.get("/byg-carport", ctx -> {

            User user = ctx.sessionAttribute("currentUser");

            if (user == null) {

                // gem hvor brugeren ville hen
                ctx.sessionAttribute("redirectAfterLogin", "/byg-carport");

                // send til login
                ctx.redirect("/login");
                return;
            }

            CarportController.showBuildPage(ctx);
        });


        app.get("/færdige-modeller", ctx -> ctx.render("ready-models.html"));
        app.get("/choose-dimensions", CarportController::showMeasurementPage);
        app.post("/carport/order", CarportController::createOrder);
    }

    private static void showBuildPage(Context ctx) {
        ctx.render("build-carport.html");
    }

    private static void showMeasurementPage(Context ctx) {
        setDimensionAttributes(ctx, null);
        ctx.render("choose-dimensions.html");
    }

    private static void createOrder(Context ctx) {
        CarportForm form = null;

        try {
            form = readCarportForm(ctx);
            CarportService carportService = new CarportService();
            Carport carport = carportService.createCarportFromForm(form);

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
            setDimensionAttributes(ctx, form);
            ctx.render("choose-dimensions.html");
        }
    }

    private static CarportForm readCarportForm(Context ctx) {
        int width = parseRequiredInt(ctx, "bredde");
        int length = parseRequiredInt(ctx, "længde");
        int height = parseRequiredInt(ctx, "højde");
        boolean hasShed = "yes".equals(ctx.formParam("redskabsrum"));
        int shedWidth = parseOptionalInt(ctx.formParam("skurBredde"));
        int shedLength = parseOptionalInt(ctx.formParam("skurLængde"));

        return new CarportForm(width, length, height, hasShed, shedWidth, shedLength);
    }

    private static void setDimensionAttributes(Context ctx, CarportForm form) {
        ctx.attribute("widthOptions", CarportDimensions.WIDTH_OPTIONS);
        ctx.attribute("lengthOptions", CarportDimensions.LENGTH_OPTIONS);
        ctx.attribute("heightOptions", CarportDimensions.HEIGHT_OPTIONS);
        ctx.attribute("shedWidthOptions", CarportDimensions.SHED_WIDTH_OPTIONS);
        ctx.attribute("shedLengthOptions", CarportDimensions.SHED_LENGTH_OPTIONS);

        ctx.attribute("selectedWidth", form == null ? CarportDimensions.DEFAULT_WIDTH : form.getWidth());
        ctx.attribute("selectedLength", form == null ? CarportDimensions.DEFAULT_LENGTH : form.getLength());
        ctx.attribute("selectedHeight", form == null ? CarportDimensions.DEFAULT_HEIGHT : form.getHeight());
        ctx.attribute("selectedHasShed", form == null || form.isHasShed());
        ctx.attribute("selectedShedWidth", form == null ? CarportDimensions.DEFAULT_SHED_WIDTH : form.getShedWidth());
        ctx.attribute("selectedShedLength", form == null ? CarportDimensions.DEFAULT_SHED_LENGTH : form.getShedLength());
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
