package app;

import app.config.SessionConfig;
import app.config.ThymeleafConfig;
import app.controllers.CarportController;
import app.controllers.MainController;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main
{

    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String URL = "jdbc:postgresql://localhost:5432/%s?currentSchema=public";
    private static final String DB = "Carport";

    private static final ConnectionPool connectionPool = ConnectionPool.getInstance(USER, PASSWORD, URL, DB);




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

        MainController.addRoutes(app,connectionPool);
        CarportController.addRoutes(app,connectionPool);

        app.start(7070);
    }
}
