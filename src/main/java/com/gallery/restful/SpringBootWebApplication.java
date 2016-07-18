package com.gallery.restful;

import com.gallery.restful.controller.PhotoController;
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
    private static final Logger LOG = LoggerFactory.getLogger(SpringBootWebApplication.class);

    /**
     * Delegates to Spring Boot’s SpringApplication class by calling run.
     * SpringApplication will bootstrap application, starting Spring which
     * will in turn start the auto-configured Tomcat web server.
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        LOG.info("starting spring application ...");
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

    /**
     * When spring application starts removes existing
     * server folder and creates new one.
     * @param args command line arguments.
     * @throws Exception on error.
     */
    @Override
    public void run(String... args) throws Exception {
        LOG.info("removing server side upload folder ...");
        FileSystemUtils.deleteRecursively(new File(PhotoController.ROOT));

        LOG.info("creating server side upload folder ...");
        Files.createDirectory(Paths.get(PhotoController.ROOT));
    }
}
