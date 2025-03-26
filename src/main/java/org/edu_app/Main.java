package org.edu_app;

import lombok.Getter;
import org.edu_app.utils.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    @Getter
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Getter
    private static final DatabaseManager dbManager = new DatabaseManager();



    public static void main(String[] args) {
        dbManager.connect();

        dbManager.executeScriptFile("saves/db/deletion.sql");
        dbManager.executeScriptFile("saves/db/creation.sql");
        dbManager.executeScriptFile("saves/db/insert.sql");

        logger.info("Starting application!");
        var context = SpringApplication.run(Main.class, args);

        context.registerShutdownHook();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Application shutting down. Closing database connection...");
            dbManager.disconnect();
            logger.info("Database connection closed.");
        }));
    }
}
