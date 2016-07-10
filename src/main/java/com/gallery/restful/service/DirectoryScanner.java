package com.gallery.restful.service;

import com.gallery.restful.controller.PhotoController;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Provides directory scan to find any files.
 */
public class DirectoryScanner {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryScanner.class);

    /**
     * Files extensions to be searched.
     */
    private static final String[] EXTENSIONS = new String[] { "png" };

    /**
     * Searches through given directory and subdirectories
     * for files and returns them.
     * @param path path to directory.
     * @return retrieved files.
     */
    public static List<File> search(String path) {
        LOG.debug("starting search for files with extensions: {}", Arrays.asList(EXTENSIONS));

        File directory = new File(path);

        List<File> files = (List<File>) FileUtils.listFiles(directory, EXTENSIONS, true);

        LOG.debug("in folder: {} found {} files", path, files.size());
        return files;
    }

    /**
     * Generate useful links to operate on a web page
     * with attributes such as rel and href.
     * @param files generate links for this files.
     * @return list of links.
     */
    public static List<Link> generateLinks(List<File> files) {
        Link link;
        List<Link> links = new ArrayList<>();

        for (File file : files) {
            link = linkTo(methodOn(PhotoController.class)
                    .loadFile(file.getName()))
                    .withRel(file.getName());
            LOG.debug("creating link: {}", link);
            links.add(link);
        }
        return links;
    }
}
