package com.gallery.restful.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller is used to manage all incoming requests.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    /**
     * Redirecting from root path to front page.
     * @return redirect link.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public final String redirecting() {
        return "redirect:/photo";
    }

    /**
     * Mapping for starting page.
     * @return view name.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    public final String frontPage() {
        return "front-page";
    }

    /**
     * Mapping for gallery page.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.POST)
    public final String galleryPage() {
        return "gallery-page";
    }
}
