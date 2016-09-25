package com.gallery.controller;

import com.gallery.service.StorageService;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
public class PhotoController {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);

    private static final int DEFAULT_RESOLUTION = 200;

    private List<Link> links;

    private StorageService storageService;

    @Autowired
    public PhotoController(final StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String redirectToHomePage() {
        return "redirect:/photo";
    }

    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    public ModelAndView renderHomePage() {
        LOG.info("Rendering home page...");
        return this.getDefaultHomeModel();
    }

    /**
     * Copies all files found in the system by provided path
     * to the server storage.
     * @param path path to folder, which contains photos.
     * @return redirection to gallery page.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.POST)
    public String loadPictures(final @RequestParam String path) {
        storageService.save(path);

        links = storageService.loadAll()
                .map(p -> linkTo(methodOn(PhotoController.class)
                        .loadPicture(p.getFileName().toString()))
                        .withRel(p.getFileName().toString()))
                .collect(Collectors.toList());

        LOG.info("Redirecting to gallery-page...");
        return "redirect:/photo/gallery";
    }

    @RequestMapping(value = "/photo/gallery", method = RequestMethod.GET)
    public ModelAndView renderGalleryPage() {
        LOG.info("Rendering gallery page ...");
        return this.getDefaultGalleryModel();
    }

    /**
     * Loads files from server storage and generates response.
     * @param filename name of the file to be fetched from the resources.
     * @return response with status 200 (OK).
     */
    @RequestMapping(value = "/photo/gallery/picture/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> loadPicture(final @PathVariable String filename) {
        return ResponseEntity.ok(storageService.loadAsResource(filename));
    }

    /**
     * Changes default picture resolution to custom one according to given parameters.
     * @param width new width of the picture.
     * @param height nwe height of the picture.
     * @return gallery model with transformed images.
     */
    @RequestMapping(value = "/photo/gallery/wh/{width}x{height}", method = RequestMethod.GET)
    public ModelAndView resizePicture(final @PathVariable String width,
                                      final @PathVariable String height) {
        ModelAndView model = this.getDefaultGalleryModel();

        model.addObject("width", width);
        model.addObject("height", height);

        LOG.debug("Setting picture size: {} x {}", width, height);

        return model;
    }

    @RequestMapping(value = "/photo/gallery/darkbackground", method = RequestMethod.GET)
    public ModelAndView applyDarkTheme() {
        ModelAndView model = this.getDefaultGalleryModel();

        LOG.info("Applying dark theme...");

        model.addObject("isDark", true);

        return model;
    }

    @RequestMapping(value = "/photo/gallery/original", method = RequestMethod.GET)
    public ModelAndView applyOriginalResolution() {
        ModelAndView model = this.getDefaultGalleryModel();

        LOG.trace("Resizing pictures to its original resolution...");

        model.addObject("isOriginal", true);

        return model;
    }

    private ModelAndView getDefaultGalleryModel() {
        ModelAndView model = new ModelAndView("index");

        if (links == null) {
            model.addObject("links", new ArrayList<>());
        } else {
            model.addObject("links", links);
        }

        model.addObject("gallery", true);
        model.addObject("width", DEFAULT_RESOLUTION);
        model.addObject("height", DEFAULT_RESOLUTION);

        return model;
    }

    private ModelAndView getDefaultHomeModel() {
        ModelAndView model = new ModelAndView("index");

        model.addObject("home", true);

        return model;
    }
}
