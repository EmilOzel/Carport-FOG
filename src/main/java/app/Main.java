package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.AdminController;
import app.controllers.CarportController;
import app.controllers.DrawingController;
import app.controllers.MainController;
import app.controllers.OrderController;
import app.controllers.UserController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {

    private static final String DB_USER     = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");
    private static final String DB_URL      = System.getenv("DB_URL");
    private static final String DB_NAME     = System.getenv("DB_NAME");

    public static final ConnectionPool connectionPool =
            ConnectionPool.getInstance(DB_USER, DB_PASSWORD, DB_URL, DB_NAME);

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
        DrawingController.addRoutes(app);
        OrderController.addRoutes(app, connectionPool);
        UserController.addRoutes(app, connectionPool);
        AdminController.addRoutes(app, connectionPool);
        UserController.addRoutes(app, connectionPool);
        app.start(7070);


    }

}
