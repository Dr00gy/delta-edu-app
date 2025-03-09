package org.edu_app;import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import java.time.LocalDate;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting application");

        try {
            // From Java 10, you can use var instead of declaring the type explicitly
            var resolver = new ClassLoaderTemplateResolver();
            resolver.setTemplateMode(TemplateMode.HTML);
            resolver.setCharacterEncoding("UTF-8");
            resolver.setPrefix("/templates/");
            resolver.setSuffix(".html");
            logger.debug("Template resolver configured with prefix: {}, suffix: {}",
                    resolver.getPrefix(), resolver.getSuffix());

            var context = new Context();
            context.setVariable("name", "Bunny");
            context.setVariable("date", LocalDate.now().toString());
            logger.debug("Context prepared with name: {}, date: {}",
                    context.getVariable("name"), context.getVariable("date"));

            var templateEngine = new TemplateEngine();
            templateEngine.setTemplateResolver(resolver);
            logger.info("Template engine initialized");

            var result = templateEngine.process("index", context);
            logger.info("Template processed successfully");

            System.out.println(result);
        } catch (Exception e) {
            logger.error("Error processing template", e);
        }

        logger.info("Application completed");
    }
}