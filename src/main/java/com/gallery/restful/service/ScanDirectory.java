package com.gallery.restful.service;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Provides directory scan to find any files.
 */
public class ScanDirectory {

    /**
     * Searches through given directory and subdirectories for
     * files with ".png" extension and returns them.
     * @param path path to directory
     * @return retrieved files
     */
    public static List<File> findPNGFiles(String path) {
        File directory = new File(path);
        String[] extensions = new String[] { "png" };

        return (List<File>) FileUtils.listFiles(directory, extensions, true);
    }
}
