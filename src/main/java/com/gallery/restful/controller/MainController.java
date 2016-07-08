package com.gallery.restful.controller;

import com.gallery.restful.service.ScanDirectory;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

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
    public final String redirectingToFrontPage() {
        return "redirect:/photo";
    }

    /**
     * Mapping for starting page.
     * @return view name.
     */
    @RequestMapping(value = "/photo", method = RequestMethod.GET)
    public final String viewFrontPage() {
        return "front-page";
    }

    /**
     * Mapping for gallery page submit form.
     * @param folderPath path to folder, which to provide photos.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.POST)
    public final String addPhotos(@RequestParam("path") String folderPath, Model model) {

        FileInputStream fileInputStream;
        int counter = 0;

        List<File> files = ScanDirectory.findPNGFiles(folderPath);

        if (files != null && files.size() > 0) {
            try {
                for (File file : files) {
                    String fileName = file.getName();

                    fileInputStream = new FileInputStream(file);

                    byte[] bytes = IOUtils.toByteArray(fileInputStream);

                    BufferedOutputStream buffStream =
                            new BufferedOutputStream(new FileOutputStream(new File("/home/ave/Downloads/" + fileName)));
                    buffStream.write(bytes);
                    buffStream.close();

                    model.addAttribute("msg", ++counter + " files uploaded");
                }
            } catch (Exception e) {
                model.addAttribute("msg", "error");
            }
        }
        return "gallery-page";
    }

    /**
     * Mapping for gallery info page.
     * @return view name.
     */
    @RequestMapping(value = "/photo/gallery", method = RequestMethod.GET)
    public final String viewGalleryPage() {
        return "gallery-page";
    }
}
