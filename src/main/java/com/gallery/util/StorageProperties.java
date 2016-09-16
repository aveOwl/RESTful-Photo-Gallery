package com.gallery.util;

import com.gallery.service.StorageServiceImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The {@link StorageProperties} class provides
 * server storage location for {@link StorageServiceImpl} class.
 */
@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Location of the directory which store files.
     */
    private String location = "server-dir";

    /**
     * Get the location of server storage.
     * @return location of server storage.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set the location of server storage.
     * @param location the location of server storage.
     */
    public void setLocation(final String location) {
        this.location = location;
    }
}
