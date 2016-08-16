package com.gallery.service;

/**
 * A service to manage pictures upload.
 */
public interface PhotoService {

    /**
     * Copies all pictures found in folder with a given path
     * on server.
     * @param path path to folder containing pictures.
     */
    void copyAll(String path);
}
