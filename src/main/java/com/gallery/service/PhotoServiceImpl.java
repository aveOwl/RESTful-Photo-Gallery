package com.gallery.service;

import com.gallery.controller.PhotoController;
import com.gallery.util.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.gallery.controller.PhotoController.ROOT;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * The PhotoService class is the basic implementation
 * of {@link PhotoService} interface.
 */
@Service
public class PhotoServiceImpl implements PhotoService {

    /**
     * Logging system.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PhotoServiceImpl.class);

    /**
     * A {@link DirectoryScanner} instance.
     */
    private DirectoryScanner directoryScanner;

    /**
     * List of uploaded files.
     */
    private List<File> listOfFiles;

    /**
     * Injecting {@link DirectoryScanner} instance.
     * @param directoryScanner instance.
     */
    @Autowired
    public void setDirectoryScanner(DirectoryScanner directoryScanner) {
        this.directoryScanner = directoryScanner;
    }

    /**
     * Finds all photos in directory by given path with the help of
     * {@link DirectoryScanner} and copies them on server.
     * @param path path to folder containing photos.
     */
    @Override
    public void copyAll(final String path) {
        FileSystemUtils.deleteRecursively(new File(ROOT));

        LOG.debug("Loading files from directory by given path: {}", path);

        listOfFiles = directoryScanner.search(path);

        if (listOfFiles != null && !listOfFiles.isEmpty()) {
            LOG.debug("Loaded {} files", listOfFiles.size());
            try {
                Files.createDirectory(Paths.get(ROOT));

                for (File file : listOfFiles) {
                    InputStream inputStream = new FileInputStream(file);
                    LOG.debug("Copying file: {} to server", file.getName());
                    Files.copy(inputStream, Paths.get(ROOT, file.getName()));
                }
            } catch (IOException | RuntimeException e) {
                LOG.error("Error while copying/reading file: {}", e.getMessage());
            }
        }
    }

    /**
     * Produces links that point to <code>@RestController</code>
     * endpoints.
     * @return list of links.
     */
    @Override
    public List<Link> generateLinks() {
        List<Link> links = new ArrayList<>();

        if (listOfFiles == null) {
            throw new NullPointerException("File list is null. Never searched for files.");
        }

        for (File file : listOfFiles) {
            Link link = linkTo(methodOn(PhotoController.class)
                    .loadFile(file.getName()))
                    .withRel(file.getName());
            LOG.debug("Creating link: {}", link);
            links.add(link);
        }
        return links;
    }
}
