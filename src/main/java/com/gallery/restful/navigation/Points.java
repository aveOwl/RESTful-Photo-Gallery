package com.gallery.restful.navigation;

import com.gallery.restful.controller.PhotoController;

/**
 * Defines constants to describe url's for
 * {@link PhotoController}.
 */
public class Points {
    private Points() { } // Prevents instantiation

    public static final String HOME = "/photo";
    public static final String GALLERY = HOME + "/gallery";
    public static final String SINGLE_PICTURE = GALLERY + "/picture/{filename:.+}";
    public static final String CUSTOM_RESOLUTION = GALLERY + "/wh/{width}x{height}";
    public static final String DARK_THEME = GALLERY + "/darkbackground";
}
