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
     * Uploads all files found in directory corresponding to provided path.
     * @param path path to server storage.
     */
    void save(Path path);

    /**
     * Loads all files from the server storage.
     * @return stream of file names.
     */
    Stream<Path> loadAll();

    /**
     * Loads file from server storage as a resource.
     * @param fileName name of the file to be loaded.
     * @return resource containing the file.
     */
    Resource loadAsResource(String fileName);
}
