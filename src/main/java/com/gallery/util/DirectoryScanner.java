package com.gallery.util;

import com.gallery.controller.PhotoController;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Provides directory scan to find any files.
 */
@Component
public class DirectoryScanner {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(DirectoryScanner.class);

    /**
     * Files extensions to be searched.
     */
    private static final String[] EXTENSIONS = new String[] {"png"};

    /**
     * Searches through given directory and subdirectories
     * for files and returns them.
     * @param path path to directory.
     * @return retrieved files.
     */
    public List<File> search(String path) {
        LOG.debug("Searching for files with extensions: {}", Arrays.asList(EXTENSIONS));

        File directory = new File(path);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Inputted path does not point to any existing directory.");
        }

        List<File> files = (List<File>) FileUtils.listFiles(directory, EXTENSIONS, true);
        LOG.debug("In folder: {} found {} files", path, files.size());
        return files;
    }
}
