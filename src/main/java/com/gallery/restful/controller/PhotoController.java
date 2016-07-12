package com.gallery.restful.controller;

import com.gallery.restful.service.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Controller is used to manage all incoming requests.
 */
@Controller
@EnableAutoConfiguration
public class PhotoController {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoController.class);

    /**
     * Server side folder where upload files store.
     */
    public static final String ROOT = "upload-dir";

    /**
     * Loading resources for classpath.
     */
    private final ResourceLoader resourceLoader;

    /**
     * Initializing resourceLoader.
     * @param resourceLoader initialized.
     */
    @Autowired
    public PhotoController(ResourceLoader resourceLoader) {
        LOG.info("initializing resourceLoader");
        this.resourceLoader = resourceLoader;
    }

    /**
     * Redirecting from root path to front page.
     * @return redirect link.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public final String redirectingToFrontPage() {
        LOG.info("redirecting from root path to main page");
        return "redirect:/photo";
    }

    /**
     * Main page on GET request.
     * @return view name.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    public final String viewFrontPage() {
        LOG.info("rendering main page");
        return "front-page";
    }

    /**
     * Retrieves files from server-ROOT folder, transports
     * respective links to template.
     * @param model to add attributes.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.GET)
    public String generateHATEOASLinks(Model model) {

        LOG.info("generating Spring HATEOAS links ...");

        List<File> files = DirectoryScanner.search(ROOT);

        model.addAttribute("links", DirectoryScanner.generateLinks(files));

        LOG.info("rendering gallery-page");
        return "gallery-page";
    }

    /**
     * Mapping for gallery page submit form.
     * @param path path to folder, which to provide photos.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.POST)
    public String addPhotos(@RequestParam String path,
                                  RedirectAttributes redirectAttributes) {
        int k = 1;
        LOG.debug("loading files from directory by given path: {}", path);
        List<File> files = DirectoryScanner.search(path);

        if (files != null && files.size() > 0) {
            LOG.debug("load {} files", files.size());
            try {
                for (File file : files) {
                    InputStream inputStream = new FileInputStream(file);
                    LOG.debug("copying file: {} to upload-ROOT directory: {}", file.getName(), ROOT);
                    Files.copy(inputStream, Paths.get(ROOT, file.getName()));
                    redirectAttributes.addFlashAttribute("msg", k++ + " files uploaded");
                }
            } catch (IOException|RuntimeException e) {
                LOG.error("error while copying/reading file: {}, with message: {}",
                        files.get(k).getName(), e.getMessage());
                redirectAttributes.addFlashAttribute("msg", "Failed to upload " +
                        files.get(k).getName() + " due to " + e.getMessage());
            }
        }
        redirectAttributes.addFlashAttribute("message", "Failed to upload because file was empty");
        LOG.info("redirecting to gallery-page");
        return "redirect:/photo/gallery";
    }

    /**
     * Loads files from server-ROOT folder and generates response.
     * @param filename name of the file to be fetched from resources.
     * @return response with status 200 (OK) if no exception occur
     * with status 404 (Not Found) otherwise.
     */
    @RequestMapping(value = "/photo/gallery/picture/{filename:.+}", method = RequestMethod.GET)
    public @ResponseBody ResponseEntity<?> loadFile(@PathVariable String filename) {
        try {
            LOG.debug("loading resource with name: {}", filename);
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            LOG.error("failed to load resource with name: {} error message: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Resizing each photo in gallery according to given parameters.
     * @param width width of the picture.
     * @param height heigth of the picture.
     * @param redirectAttributes width and height on to the gallery page.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery/wh/{width}x{height}", method = RequestMethod.GET)
    public String resize(@PathVariable String width, @PathVariable String height,
                         RedirectAttributes redirectAttributes) {
        LOG.debug("setting photo size: {} x {}", width, height);
        redirectAttributes.addFlashAttribute("width", width);
        redirectAttributes.addFlashAttribute("height", height);

        return "redirect:/photo/gallery";
    }
}
