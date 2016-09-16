package com.gallery;

import com.gallery.service.StorageService;
import com.gallery.service.StorageServiceImpl;
import com.gallery.util.StorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Launches spring application.
 */
@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
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

    /**
     * Initialize server storage directory.
     * @param storageService service to process over storage.
     * @return initialized storage directory.
     */
    @Bean
    CommandLineRunner init(final StorageService storageService) {
        return (args) -> {
            LOG.info("Initializing server storage...");
            storageService.deleteAll();
            storageService.init();
        };
    }
}
