package com.gallery.restful;

import com.gallery.restful.controller.MainController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Launches spring application.
 */
@SpringBootApplication
public class SpringBootWebApplication implements CommandLineRunner {
    /**
     * Logging system.
     */
    public static final Logger LOG = LoggerFactory.getLogger(SpringBootWebApplication.class);

    /**
     * Delegates to Spring Boot’s SpringApplication class by calling run.
     * SpringApplication will bootstrap our application, starting Spring which
     * will in turn start the auto-configured Tomcat web server.
     * @throws Exception
     * @param args command-line arguments
     * @throws Exception if errors occur
     */
    public static void main(final String[] args) throws Exception {
        LOG.info("starting spring application");
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("deleting ROOT directory");
        FileSystemUtils.deleteRecursively(new File(MainController.ROOT));

        LOG.info("creating new ROOT directory");
        Files.createDirectory(Paths.get(MainController.ROOT));
    }
}
