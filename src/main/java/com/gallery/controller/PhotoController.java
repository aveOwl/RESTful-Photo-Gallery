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

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping(value = "/photo")
public class PhotoController {
    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);
    private static final int DEFAULT_RESOLUTION = 200;

    private List<Link> links = new ArrayList<>();
    private StorageService storageService;

    @Autowired
    public PhotoController(final StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView renderHomePage() {
        LOG.info("Rendering home page...");
        return this.getDefaultHomeModel();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String renderGalleryPageWithUploadedPictures(final @RequestParam String path) {
        this.storageService.save(Paths.get(path));

        this.links = this.storageService.loadAll()
                .map(p -> linkTo(methodOn(PhotoController.class)
                        .renderSinglePicture(p.getFileName().toString()))
                        .withRel(p.getFileName().toString()))
                .collect(Collectors.toList());

        LOG.info("Redirecting to gallery-page...");
        return "redirect:/photo/gallery";
    }

    @RequestMapping(value = "/gallery", method = RequestMethod.GET)
    public ModelAndView renderGalleryPage() {
        LOG.info("Rendering gallery page ...");
        return this.getDefaultGalleryModel();
    }

    @RequestMapping(value = "/gallery/picture/{filename:.+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Resource> renderSinglePicture(final @PathVariable String filename) {
        return ResponseEntity.ok(this.storageService.loadAsResource(filename));
    }

    @RequestMapping(value = "/gallery/wh/{width}x{height}", method = RequestMethod.GET)
    public ModelAndView resizePicturesOnGalleryPage(final @PathVariable String width,
                                                    final @PathVariable String height) {
        final ModelAndView model = this.getDefaultGalleryModel();

        model.addObject("width", width);
        model.addObject("height", height);

        LOG.debug("Setting picture size: {} x {}", width, height);

        return model;
    }

    @RequestMapping(value = "/gallery/darkbackground", method = RequestMethod.GET)
    public ModelAndView renderGalleryPageWithBlackBackground() {
        final ModelAndView model = this.getDefaultGalleryModel();

        LOG.info("Applying dark theme...");

        model.addObject("isDark", true);

        return model;
    }

    @RequestMapping(value = "/gallery/original", method = RequestMethod.GET)
    public ModelAndView renderGalleryPageWithPicturesInOriginalResolution() {
        final ModelAndView model = this.getDefaultGalleryModel();

        LOG.trace("Resizing pictures to its original resolution...");

        model.addObject("isOriginal", true);

        return model;
    }

    private ModelAndView getDefaultGalleryModel() {
        final ModelAndView model = new ModelAndView("index");

        model.addObject("links", links);
        model.addObject("gallery", true);
        model.addObject("width", DEFAULT_RESOLUTION);
        model.addObject("height", DEFAULT_RESOLUTION);

        return model;
    }

    private ModelAndView getDefaultHomeModel() {
        final ModelAndView model = new ModelAndView("index");

        model.addObject("home", true);

        return model;
    }
}
