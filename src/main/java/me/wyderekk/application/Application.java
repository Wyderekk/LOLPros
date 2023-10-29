package me.wyderekk.application;

import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import me.wyderekk.application.data.database.SQLite;
import me.wyderekk.application.discord.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */

@SpringBootApplication
@Theme(value = "application", variant = "dark")
@NpmPackage(value = "@vaadin/router", version = "1.7.5")
@PWA(name = "Streamer LOLPros", shortName = "LOLPros")
public class Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        SQLite.connect();
        Core.runBot();
    }
}
