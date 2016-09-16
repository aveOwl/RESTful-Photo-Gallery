package com.gallery.controller;

import com.gallery.service.StorageService;
import com.gallery.service.StorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * The {@link PhotoController} is a RESTful web service controller.
 */
@Controller
public class PhotoController {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);

    /**
     * Default picture resolution.
     */
    private static final int DEFAULT_SIZE = 200;

    /**
     * List of useful links to files.
     */
    private static List<Link> links;

    /**
     * {@link StorageService} class.
     */
    private StorageService storageService;

    /**
     * Initializing {@link StorageServiceImpl} instance.
     * @param storageService instance of {@link StorageServiceImpl}.
     */
    @Autowired
    public PhotoController(final StorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * Redirecting root path to home page.
     * @return redirection to home page.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String redirect() {
        return "redirect:/photo";
    }

    /**
     * Default home page.
     * @return home model.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    public ModelAndView home() {
        LOG.info("Rendering home page...");
        return this.getHomeModel();
    }

    /**
     * Copies all files found in the system by provided path
     * to the server storage.
     * @param path path to folder, which contains photos.
     * @return redirection to gallery page.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.POST)
    public String addPictures(@RequestParam String path) {

        storageService.save(path);

        links = storageService.loadAll()
                .map(p -> linkTo(methodOn(PhotoController.class)
                        .loadFile(p.getFileName().toString()))
                        .withRel(p.getFileName().toString()))
                .collect(Collectors.toList());

        LOG.info("Redirecting to gallery-page...");
        return "redirect:/photo/gallery";
    }

    /**
     * Default gallery page.
     * @return gallery model.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.GET)
    public ModelAndView generateLinks() {
        LOG.info("Rendering gallery page ...");
        return getGalleryModel();
    }

    /**
     * Loads files from server storage and generates response.
     * @param filename name of the file to be fetched from the resources.
     * @return response with status 200 (OK).
     */
    @RequestMapping(value = "/photo/gallery/picture/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> loadFile(@PathVariable String filename) {
        return ResponseEntity.ok(storageService.loadAsResource(filename));
    }

    /**
     * Changes default picture resolution to custom one according to given parameters.
     * @param width width of the picture.
     * @param height height of the picture.
     * @return gallery model with transformed images.
     */
    @RequestMapping(value = "/photo/gallery/wh/{width}x{height}", method = RequestMethod.GET)
    public ModelAndView resize(@PathVariable String width,
                               @PathVariable String height) {
        ModelAndView model = getGalleryModel();

        model.addObject("width", width);
        model.addObject("height", height);

        LOG.debug("Setting picture size: {} x {}", width, height);

        return model;
    }

    /**
     * Applies dark background.
     * @return gallery model with dark background.
     */
    @RequestMapping(value = "/photo/gallery/darkbackground", method = RequestMethod.GET)
    public ModelAndView showDark() {
        ModelAndView model = getGalleryModel();

        LOG.info("Applying dark background...");

        model.addObject("isDark", true);

        return model;
    }

    /**
     * Transforms every picture to its original resolution.
     * @return gallery model with images in original resolution.
     */
    @RequestMapping(value = "/photo/gallery/original", method = RequestMethod.GET)
    public ModelAndView showOriginal() {
        ModelAndView model = getGalleryModel();

        LOG.trace("Resizing pictures to its original resolution...");

        model.addObject("isOriginal", true);

        return model;
    }

    /**
     * Default model for building gallery page.
     * @return default gallery model.
     */
    private ModelAndView getGalleryModel() {
        ModelAndView model = new ModelAndView("index");

        model.addObject("links",
                links == null ? new ArrayList<>() : links);

        model.addObject("gallery", true);
        model.addObject("width", DEFAULT_SIZE);
        model.addObject("height", DEFAULT_SIZE);

        return model;
    }

    /**
     * Default model for building home page.
     * @return default home model.
     */
    private ModelAndView getHomeModel() {
        ModelAndView model = new ModelAndView("index");

        model.addObject("home", true);

        return model;
    }
}
