package com.spring.boot.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main Controller.
 */
@RestController
@EnableAutoConfiguration
public class MainController {

    /**
     * Displaying front view.
     * @return view name.
     */
    @RequestMapping(value = "/")
    public final String showHome() {
        return "Hello Spring Boot Again";
    }

    /**
     * Start point.
     * @throws Exception
     * @param args arguments
     * @throws Exception if something happens
     */
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(MainController.class, args);
    }
}
