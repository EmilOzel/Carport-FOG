package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.CarportController;
import app.controllers.MainController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.jetty.modifyServletContextHandler(
                    handler -> handler.setSessionHandler(SessionConfig.sessionConfig())
            );
            config.fileRenderer(new JavalinThymeleaf(ThymeleafConfig.templateEngine()));
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
        });

        MainController.addRoutes(app);
        CarportController.addRoutes(app);

        app.start(7070);
    }
}
