package com.gallery.util;

import com.gallery.service.StorageServiceImpl;

/**
 * The {@link StorageException} is the custom class
 * for providing exception information for {@link StorageServiceImpl}
 * class.
 */
public class StorageException extends RuntimeException {

    /**
     * Convenient constructor for providing
     * {@link StorageException} instance with exception message.
     * @param message exception message.
     */
    public StorageException(final String message) {
        super(message);
    }

    /**
     * Convenient constructor for providing
     * {@link StorageException} instance with exception message
     * and cause.
     * @param message exception message.
     * @param cause exception cause.
     */
    public StorageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
