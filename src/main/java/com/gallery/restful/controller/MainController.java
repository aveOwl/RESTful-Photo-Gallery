package com.gallery.restful.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller is used to manage all incoming requests.
 */
@Controller
@EnableAutoConfiguration
public class MainController {
    /**
     * Displaying home page.
     * @return view name.
     */
    @RequestMapping(value = "/")
    public final String start() {
        return "index";
    }
}
