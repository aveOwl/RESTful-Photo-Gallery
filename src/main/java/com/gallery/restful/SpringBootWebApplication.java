package com.gallery.restful;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Launches spring application.
 */
@SpringBootApplication
public class SpringBootWebApplication {
    /**
     * Delegates to Spring Boot’s SpringApplication class by calling run.
     * SpringApplication will bootstrap our application, starting Spring which
     * will in turn start the auto-configured Tomcat web server.
     * @throws Exception
     * @param args command-line arguments
     * @throws Exception if errors occur
     */
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}
