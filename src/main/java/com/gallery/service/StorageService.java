package com.gallery.service;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * <p>
 *     The {@link StorageService} interface defines all public business behaviours
 *     associated with file operations such as uploading, storing and loading
 *     multiple files.
 * </p>
 * <p>
 *     This interface should be injected into StorageService clients, not the
 *     {@link StorageServiceImpl} class.
 * </p>
 */
public interface StorageService {

    /**
     * Creates storage directory.
     */
    void init();

    /**
     * Removes storage directory and all inner directories
     * recursively.
     */
    void deleteAll();

    /**
     * Uploads all files found in directory corresponding to provided
     * path to server storage.
     * @param path path to directory containing files for upload.
     */
    void save(String path);

    /**
     * Returns path to file on server storage.
     * @param fileName name of the file to be loaded.
     * @return relative path to file on server storage.
     */
    Path load(String fileName);

    /**
     * Loads all files from the server storage.
     * @return stream of file names.
     */
    Stream<Path> loadAll();

    /**
     * Loads file from server storage as a resource.
     * @param fileName name of the file to be loaded.
     * @return resource containing file to load.
     */
    Resource loadAsResource(String fileName);
}
