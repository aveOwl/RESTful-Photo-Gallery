package com.gallery.util;

import com.gallery.service.StorageServiceImpl;

/**
 * The {@link StorageFileNotFoundException} is the custom class
 * for providing exception information for {@link StorageServiceImpl}
 * class.
 */
public class StorageFileNotFoundException extends StorageException {

    /**
     * Convenient constructor for providing
     * {@link StorageFileNotFoundException} instance with exception message.
     * @param message exception message.
     */
    public StorageFileNotFoundException(final String message) {
        super(message);
    }

    /**
     * Convenient constructor for providing
     * {@link StorageFileNotFoundException} instance with exception message
     * and cause.
     * @param message exception message.
     * @param cause exception cause.
     */
    public StorageFileNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
