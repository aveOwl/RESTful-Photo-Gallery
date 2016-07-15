package com.gallery.restful.service;

import com.gallery.restful.controller.PhotoController;
import com.gallery.restful.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.gallery.restful.controller.PhotoController.ROOT;

/**
 * Basic implementation for {@link PhotoService} interface.
 */
@Service
public class PhotoServiceImpl implements PhotoService {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoServiceImpl.class);

    /**
     * Finds all photos in directory by given path with the help of
     * {@link DirectoryScanner} and copies them on server.
     * @param path path to folder containing photos.
     */
    @Override
    public void copyAll(String path) {
        LOG.debug("loading files from directory by given path: {}", path);

        List<File> files = DirectoryScanner.search(path);

        if (files != null && files.size() > 0) {
            LOG.debug("load {} files", files.size());
            try {
                for (File file : files) {
                    InputStream inputStream = new FileInputStream(file);
                    LOG.debug("copying file: {} to server", file.getName());
                    Files.copy(inputStream, Paths.get(ROOT, file.getName()));
                }
            } catch (IOException | RuntimeException e) {
                e.printStackTrace();
                LOG.error("error while copying/reading file: {}", e.getMessage());
            }
        }
    }
}
