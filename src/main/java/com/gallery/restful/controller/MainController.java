package com.gallery.restful.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import com.gallery.restful.service.ScanDirectory;
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
import java.util.stream.Collectors;

/**
 * Controller is used to manage all incoming requests.
 */
@Controller
@EnableAutoConfiguration
public class MainController {

    /**
     * Logging system.
     */
    public static final Logger LOG = LoggerFactory.getLogger(MainController.class);

    /**
     * Folder where upload files store.
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
    public MainController(ResourceLoader resourceLoader) {
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

    @RequestMapping(value = "/photo/gallery", method = RequestMethod.GET)
    public String provideUploadInfo(Model model) throws IOException {

        LOG.info("generating Spring HATEOAS links");
        model.addAttribute("files", Files.walk(Paths.get(ROOT))
                .filter(path -> !path.equals(Paths.get(ROOT)))
                .map(path -> Paths.get(ROOT).relativize(path))
                .map(path -> linkTo(methodOn(MainController.class).getFile(path.toString())).withRel(path.toString()))
                .collect(Collectors.toList()));

        LOG.info("rendering gallery-page");
        return "gallery-page";
    }

    @RequestMapping(value = "/photo/gallery/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> getFile(@PathVariable String filename) {
        LOG.debug("loading resource by file name: {}", filename);
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(ROOT, filename).toString()));
        } catch (Exception e) {
            LOG.error("failed to load resource by file name: {} with message: {}", filename, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Mapping for gallery page submit form.
     * @param folderPath path to folder, which to provide photos.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.POST)
    public final String addPhotos(@RequestParam("path") String folderPath,
                                  RedirectAttributes redirectAttributes) {
        int k = 1;
        LOG.debug("loading files from directory by given path: {}", folderPath);
        List<File> files = ScanDirectory.findPNGFiles(folderPath);

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
}
