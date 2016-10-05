package com.gallery.service;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * The {@link StorageService} interface defines all public business behaviours
 * associated with file operations such as uploading, storing and loading
 * multiple files.
 */
public interface StorageService {
    /**
     * Uploads all files found in directory corresponding to provided path
     * to server storage.
     *
     * @param src path to directory in which files are located.
     */
    void save(Path src);

    /**
     * Loads all files from the server storage.
     *
     * @return stream of files.
     */
    Stream<Path> loadAll();

    /**
     * Loads file from server storage as a resource.
     *
     * @param fileName name of the file to be loaded.
     * @return resource containing the file.
     */
    Resource loadAsResource(String fileName);
}
