package com.gallery.service;

import com.gallery.util.StorageException;
import com.gallery.util.StorageFileNotFoundException;
import com.gallery.util.StorageProperties;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The {@link StorageServiceImpl} class represents basic
 * implementation of {@link StorageService} interface.
 */
@Service
public class StorageServiceImpl implements StorageService {

    /**
     * Logging system for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

    /**
     * Path to server storage.
     */
    private final Path root;

    /**
     * Initializing storage root path with the help of {@link StorageProperties} class.
     * @param properties properties containing storage location.
     */
    @Autowired
    public StorageServiceImpl(final StorageProperties properties) {
        this.root = Paths.get(properties.getLocation());
        LOG.debug("Setting server storage path: {}", this.root.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        try {
            Files.createDirectory(root);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * {@inheritDoc}
     * If provided path does not point to any existing directory or
     * no files are found throws {@link StorageException}
     */
    @Override
    public void save(final String path) {
        File directory = new File(path);

        if (!directory.isDirectory()) {
            throw new StorageException("Inputted path does not point to any existing directory");
        }

        List<File> files = (List<File>) FileUtils.listFiles(directory, new String[]{"png"}, true);
        LOG.debug("In directory {} found {} files", directory.getAbsolutePath(), files.size());

        try {
            if (files.isEmpty()) {
                LOG.error("File list is empty: {}", files.size());
                throw new StorageException("Failed to store files. No files provided.");
            }
            for (File file : files) {
                InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
                Files.copy(inputStream, this.root.resolve(file.getName()), REPLACE_EXISTING);
            }
            LOG.debug("Copied {} files on server", files.size());
        } catch (IOException e) {
            LOG.error("Failed to store files {}", e.getLocalizedMessage());
            throw new StorageException("Failed to store files", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAll() {
        LOG.info("Loading files from server storage...");
        try {
            return Files.walk(this.root, 1)
                    .filter(path -> !path.equals(this.root))
                    .map(this.root::relativize);
        } catch (IOException e) {
            LOG.error("Failed during reading stored files {}", e.getLocalizedMessage());
            throw new StorageException("Failed to read stored files", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Path load(final String fileName) {
        return this.root.resolve(fileName);
    }

    /**
     * {@inheritDoc}
     * If for provided fileName no files found throws {@link StorageFileNotFoundException},
     * otherwise returns corresponding resource.
     */
    @Override
    public Resource loadAsResource(final String fileName) {
        try {
            Path file = load(fileName);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read file " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new StorageFileNotFoundException("Could not read file " + fileName);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}
