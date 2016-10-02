package com.gallery;

import com.gallery.service.DestroyService;
import com.gallery.service.InitService;
import com.gallery.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class Application extends WebMvcConfigurerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] args) {
        LOG.info("Starting spring application...");
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        LOG.info("Redirecting to home page...");
        registry.addViewController("/").setViewName("redirect:/photo");
    }

    @Bean
    CommandLineRunner init(final InitService initService,
                           final DestroyService destroyService) {
        return (args) -> {
            LOG.info("Initializing server storage...");
            destroyService.destroy();
            initService.init();
        };
    }
}
