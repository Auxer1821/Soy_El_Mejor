package server;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.google.gson.Gson;
import config.ServiceLocator;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.config.SizeUnit;
import io.javalin.http.HttpStatus;
import middlewares.AuthMiddleware;
import org.eclipse.paho.client.mqttv3.MqttException;
import server.handlers.AppHandlers;
import utils.PropertiesManager;
import utils.javalin.JavalinRenderer;
import utils.javalin.seeder.DataSeeder;
import utils.javalin.seeder.GestorBroker;
import utils.telegramSender.TelegramSender;

import java.util.concurrent.CountDownLatch;

import java.io.IOException;
import java.util.function.Consumer;

public class Server {
    private static Javalin app = null;

    private static PropertiesManager instance = new PropertiesManager("properties/server.properties");

    public static Javalin app() {
        if (app == null)
            throw new RuntimeException("App no inicializada");
        return app;
    }

    public static void init() throws MqttException {
        if (app == null) {
            Integer port = instance.getPropertyInteger("server_port");
            app = Javalin.create(config()).start(port);

            if (instance.getPropertyBoolean("dev_mode")) {
                //DataSeeder.init();
                ServiceLocator.instanceOf(GestorBroker.class).recuperar();
                //TelegramSender.startTelegramBot();
            }

            AuthMiddleware.apply(app);
            AppHandlers.applyHandlers(app);

            Router.init(app);
        }
    }

    private static Consumer<JavalinConfig> config() {
        return config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
            });

            config.jetty.multipartConfig.cacheDirectory("/public/temporal"); // where to write files that exceed the in
                                                                             // memory limit
            config.jetty.multipartConfig.maxFileSize(10, SizeUnit.MB); // the maximum individual file size allowed
            config.jetty.multipartConfig.maxInMemoryFileSize(10, SizeUnit.MB); // the maximum file size to handle in
                                                                               // memory
            config.jetty.multipartConfig.maxTotalRequestSize(1, SizeUnit.GB); // the maximum size of the entire
                                                                              // multipart request

            Handlebars handlebars = new Handlebars()
                    .with(new ClassPathTemplateLoader("/templates", ".hbs"));

            /* Helpers */

            handlebars.registerHelper("json", (context, options) -> new Gson().toJson(context));

            // Registering helpers for header and footer
            handlebars.registerHelper("header", (context, options) -> {
                try {
                    Template headerTemplate = handlebars.compile("shared/header");
                    return headerTemplate.apply(context);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Header not found"; // Fallback message
                }
            });

            handlebars.registerHelper("ifCond", (value1, options) -> {
                Object value2 = options.param(0); // El segundo valor a comparar
                if (value1 != null && value1.equals(value2)) {
                    return options.fn();
                } else {
                    return options.inverse();
                }
            });

            handlebars.registerHelper("ifNotEquals", (context, options) -> {
                Object arg1 = context;
                Object arg2 = options.param(0);

                // Comparación
                if (!arg1.equals(arg2)) {
                    return options.fn();
                } else {
                    return options.inverse();
                }
            });

            handlebars.registerHelper("mayorOIgual", (Helper<String>) (puntosAdquiridos, options) -> {
                int valorAComparar = Integer.parseInt(puntosAdquiridos);
                int valorComparado = Integer.parseInt(options.param(0));

                if (valorAComparar >= valorComparado) {
                    return options.fn();
                } else {
                    return options.inverse();
                }
            });

            handlebars.registerHelper("footer", (context, options) -> {
                try {
                    Template footerTemplate = handlebars.compile("shared/footer");
                    return footerTemplate.apply(context);
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Footer not found"; // Fallback message
                }
            });

            // Register the file renderer
            config.fileRenderer(new JavalinRenderer().register("hbs", (path, model, context) -> {
                Template template;
                try {
                    template = handlebars.compile(path.replace(".hbs", ""));
                    return template.apply(model);
                } catch (IOException e) {
                    e.printStackTrace();
                    context.status(HttpStatus.NOT_FOUND);
                    return "No se encuentra la página indicada...";
                }
            }));
        };
    }
}
