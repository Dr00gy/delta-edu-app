package org.edu_app;

import lombok.Getter;
import org.edu_app.utils.InitDBManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@SpringBootApplication
@EntityScan(basePackages = "org.edu_app.model.entity")
@EnableJpaRepositories(basePackages = "org.edu_app.repository")
public class Main {

    @Getter
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Autowired
    private InitDBManager dbManager;

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        logger.info("Application started successfully!");
    }

    // Execute scripts only when the application is fully ready
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        logger.info("Executing database scripts after application startup...");
        dbManager.connect();
        dbManager.executeScriptFile("saves/db/deletion.sql");
        dbManager.executeScriptFile("saves/db/creation.sql");
        dbManager.executeScriptFile("saves/db/insert.sql");
        logger.info("Database scripts executed successfully!");
    }
}
