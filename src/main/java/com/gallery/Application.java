package com.gallery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.gallery.controller.PhotoController.ROOT;

/**
 * Launches spring application.
 */
@SpringBootApplication
public class Application {
    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    /**
     * Delegates to Spring Boot’s SpringApplication class by calling run.
     * SpringApplication will bootstrap application, starting Spring which
     * will in turn start the auto-configured Tomcat web server.
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        LOG.info("starting spring application ...");
        SpringApplication.run(Application.class, args);
    }
}
