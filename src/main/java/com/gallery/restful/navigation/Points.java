package com.gallery.restful.navigation;

import com.gallery.restful.controller.PhotoController;

/**
 * Defines constants to describe url's for
 * {@link PhotoController}.
 */
public abstract class Points {
    /**
     * Prevents instantiation of the class.
     */
    private Points() { }

    /**
     * URL path to home page.
     */
    public static final String HOME = "/photo";

    /**
     * URL path to gallery page.
     */
    public static final String GALLERY = HOME + "/gallery";

    /**
     * URL path to single picture in gallery.
     */
    public static final String SINGLE_PICTURE = GALLERY + "/picture/{filename:.+}";

    /**
     * URL path to custom image resolution.
     */
    public static final String CUSTOM_RESOLUTION = GALLERY + "/wh/{width}x{height}";

    /**
     * URL path to dark theme gallery page.
     */
    public static final String DARK_THEME = GALLERY + "/darkbackground";

    /**
     * URL path to gallery page with photos in original resolution.
     */
    public static final String ORIGINAL = GALLERY + "/original";
}
