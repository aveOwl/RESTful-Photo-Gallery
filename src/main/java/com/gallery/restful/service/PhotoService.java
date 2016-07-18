package com.gallery.restful.service;

/**
 * A service to manage photo upload.
 */
public interface PhotoService {

    /**
     * Copy all photos found in folder with a given path
     * on server.
     * @param path path to folder containing photos.
     */
    void copyAll(String path);
}
