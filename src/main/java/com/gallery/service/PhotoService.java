package com.gallery.service;

import org.springframework.hateoas.Link;

import java.util.List;

/**
 * The PhotoService class provides functionality in order
 * to manage pictures upload.
 */
public interface PhotoService {

    /**
     * Copies all pictures found in folder with a given path
     * on server.
     * @param path path to folder containing pictures.
     */
    void copyAll(String path);

    /**
     * Generates useful links to operate on a web page
     * with attributes such as rel and href.
     * @return list of links.
     */
    List<Link> generateLinks();
}
