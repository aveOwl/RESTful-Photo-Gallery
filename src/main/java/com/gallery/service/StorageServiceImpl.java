package com.gallery.service;

import com.gallery.util.StorageException;
import com.gallery.util.StorageFileNotFoundException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
public class StorageServiceImpl implements StorageService, InitService, DestroyService {
    private static final Logger LOG = LoggerFactory.getLogger(StorageServiceImpl.class);
    private final Path storagePath = Paths.get("server-storage");
    private final String[] fileExtensions = {"png"};

    /**
     * Creates storage directory.
     */
    @Override
    public void init() {
        try {
            Files.createDirectory(this.storagePath);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void save(final Path path) {
        try {
            List<Path> files = this.findFilesWithExtensions(path, this.fileExtensions);
            this.saveFilesOnServer(files);
        } catch (IOException e) {
            LOG.error("Failed to store files {} : {}", e.getClass().getSimpleName(), e.getLocalizedMessage());
            throw new StorageException("Failed to store files", e);
        }
    }

    /**
     * Searches directory by given path for files with specified fileExtensions
     * and returns files if any found, otherwise returns empty list.
     * @param path path to directory to search files in.
     * @param extensions file fileExtensions.
     * @return list of files if any found, otherwise empty list.
     */
    private List<Path> findFilesWithExtensions(final Path path, final String[] extensions) throws IOException {
        if (!Files.isDirectory(path)) {
            throw new StorageException("Inputted path does not point to any existing directory");
        }

        List<Path> files = FileUtils.listFiles(path.toFile(), extensions, true)
                .stream()
                .map(File::toPath)
                .collect(Collectors.toList());
        LOG.debug("In directory {} found {} files", path.toAbsolutePath(), files.size());

        return files;
    }

    /**
     * If no files are provided throws {@link StorageException}, otherwise
     * copies all files to server storage.
     * @param files list of files to store.
     * @throws IOException on error.
     */
    private void saveFilesOnServer(List<Path> files) throws IOException {
        if (files.isEmpty()) {
            LOG.error("File list is empty: {}", files.size());
            throw new StorageException("Failed to store files. No files provided.");
        }

        for (Path file : files) {
            Files.copy(file, this.storagePath.resolve(file.getFileName()), REPLACE_EXISTING);
        }

        LOG.debug("Copied {} files on server", files.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Stream<Path> loadAll() {
        LOG.info("Loading files from server storage...");
        try {
            return Files.walk(this.storagePath, 1)
                    .filter(path -> !path.equals(this.storagePath))
                    .map(this.storagePath::relativize);
        } catch (IOException e) {
            LOG.error("Failed during reading stored files {}", e.getLocalizedMessage());
            throw new StorageException("Failed to read stored files", e);
        }
    }

    /**
     * {@inheritDoc}
     * If for provided fileName no files found throws {@link StorageFileNotFoundException},
     * otherwise returns corresponding resource.
     */
    @Override
    public Resource loadAsResource(final String fileName) {
        try {
            Path file = this.storagePath.resolve(fileName);
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
     * Removes storage directory and all inner directories
     * recursively.
     */
    @Override
    public void destroy() {
        FileSystemUtils.deleteRecursively(this.storagePath.toFile());
    }
}
