package com.gallery.restful.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

/**
 * Provides directory scan to find any files.
 */
public class ScanDirectory {

    /**
     * Logging system.
     */
    public static final Logger LOG = LoggerFactory.getLogger(ScanDirectory.class);

    /**
     * Searches through given directory and subdirectories for
     * files with ".png" extension and returns them.
     * @param path path to directory
     * @return retrieved files
     */
    public static List<File> findPNGFiles(String path) {
        LOG.debug("searching for files with png extension " +
                "in directory: {} and its subdirectories", path);
        File directory = new File(path);
        String[] extensions = new String[] { "png" };

        List<File> files = (List<File>) FileUtils.listFiles(directory, extensions, true);

        LOG.debug("found {} files: {}", files.size(), files);
        return files;
    }
}
