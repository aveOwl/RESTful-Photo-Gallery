package com.gallery.restful.controller;

import com.gallery.restful.service.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.gallery.restful.navigation.Points.*;

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
     * Default picture resolution.
     */
    private static final int DEFAULT_SIZE = 200;

    /**
     * Number of pictures in one row.
     */
    private static final int DEFAULT_LAYOUT = 4;

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
     * Redirecting from root path to home page.
     * @return redirect to home page.
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String root() {
        return "redirect:" + HOME;
    }

    /**
     * Default home page.
     * @return default home page model.
     */
    @RequestMapping(value = HOME, method = RequestMethod.GET)
    public ModelAndView frontPage() {
        LOG.info("rendering front page ...");
        return getFrontModel();
    }

    /**
     * Default gallery page.
     * @return gallery model with uploaded images.
     */
    @RequestMapping(value = GALLERY, method = RequestMethod.GET)
    public ModelAndView generateLinks() {
        LOG.info("rendering gallery page ...");
        return getGalleryModel();
    }

    /**
     * Evaluates given input, searches through file system using
     * provided path for files with ".png" extension, if any files
     * found copies them on server and redirects to gallery page.
     * @param path path to folder, which contains photos.
     * @return view name.
     */
    @RequestMapping(value = HOME, method = RequestMethod.POST)
    public String addPhotos(@RequestParam String path, RedirectAttributes redirectAttributes) {
        LOG.debug("loading files from directory by given path: {}", path);

        List<File> files = DirectoryScanner.search(path);

        if (files != null && files.size() > 0) {
            LOG.debug("load {} files", files.size());
            try {
                for (File file : files) {
                    InputStream inputStream = new FileInputStream(file);
                    LOG.debug("copying file: {} to server directory: {}", file.getName(), ROOT);
                    Files.copy(inputStream, Paths.get(ROOT, file.getName()));
                    redirectAttributes.addFlashAttribute("msg", files.size() + " files uploaded");
                }
            } catch (IOException|RuntimeException e) {
                LOG.error("error while copying/reading file: {}", e.getMessage());
                redirectAttributes.addFlashAttribute("msg", "Failed to upload files " + e.getMessage());
            }
        }
        redirectAttributes.addFlashAttribute("message", "Failed to upload because file was empty");
        LOG.info("redirecting to gallery-page");
        return "redirect:/photo/gallery";
    }

    /**
     * Loads files from server and generates response.
     * @param filename name of the file to be fetched from resources.
     * @return response with status 200 (OK) if no exception occur
     * with status 404 (Not Found) otherwise.
     */
    @RequestMapping(value = SINGLE_PICTURE, method = RequestMethod.GET)
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
     * @return gallery view with resized images.
     */
    @RequestMapping(value = CUSTOM_RESOLUTION, method = RequestMethod.GET)
    public ModelAndView resize(@PathVariable String width, @PathVariable String height) {
        ModelAndView model = getGalleryModel();

        model.addObject("width", width);
        model.addObject("height", height);
        LOG.debug("setting photo size: {} x {}", width, height);

        return model;
    }

    /**
     * Changing gallery theme.
     * @return gallery view with dark theme.
     */
    @RequestMapping(value = DARK_THEME, method = RequestMethod.GET)
    public ModelAndView blackGallery() {
        ModelAndView model = getGalleryModel();

        model.addObject("isDark", true);

        return model;
    }

    /**
     * Default model for building gallery page.
     * @return default view of gallery page.
     */
    private ModelAndView getGalleryModel() {
        ModelAndView model = new ModelAndView("index");

        model.addObject("gallery", true);
        model.addObject("links", DirectoryScanner.generateLinks(ROOT));
        model.addObject("width", DEFAULT_SIZE);
        model.addObject("height", DEFAULT_SIZE);
        model.addObject("picturesInRow", DEFAULT_LAYOUT);

        return model;
    }

    /**
     * Default model for building home page.
     * @return default view of home page.
     */
    private ModelAndView getFrontModel() {
        ModelAndView model = new ModelAndView("index");
        model.addObject("home", true);

        return model;
    }
}
