package com.gallery.restful.controller;

import com.gallery.restful.service.PhotoService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static com.gallery.restful.navigation.Points.DARK_THEME;
import static com.gallery.restful.navigation.Points.GALLERY;
import static com.gallery.restful.navigation.Points.HOME;
import static com.gallery.restful.navigation.Points.ORIGINAL;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * {@code @WebMvcTest} based tests
 * for {@link PhotoController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PhotoController.class)
public class PhotoControllerTest {

    /**
     * Main entry point for server-side
     * Spring MVC test support.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Mocking {@link PhotoService} behaviour.
     */
    @MockBean
    private PhotoService photoService;

    /**
     * Creating and deleting temporary folder
     * structure.
     */
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Redirection from root path onto home
     * page.
     */
    @Test
    public void shouldRedirectToHomePage() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(HOME));
    }

    /**
     * Displaying default home page view,
     * when accessed with GET request.
     */
    @Test
    public void shouldDisplayHomePage() throws Exception {
        this.mvc.perform(get(HOME))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index"));
    }

    /**
     * Displaying default gallery page view,
     * when accessed with GET request, without
     * any uploaded pictures.
     */
    @Test
    public void shouldDisplayEmptyGalleryPage() throws Exception {
        this.mvc.perform(get(GALLERY))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index"))
                .andExpect(model().attribute("gallery", true))
                .andExpect(model().attribute("links", new ArrayList<>()))
                .andExpect(model().attribute("width", 200))
                .andExpect(model().attribute("height", 200));
    }

    /**
     * Copying files from given path on server.
     * Redirecting from home page proceeding
     * POST request onto gallery page.
     * Display uploaded pictures.
     */
    @Test
    public void shouldDisplayGalleryPageWithUploadedPictures() throws Exception {
        String folderPath = temporaryFolder.newFolder().getAbsolutePath();

        this.mvc.perform(post(HOME).param("path", folderPath))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GALLERY));
        verify(photoService, atLeastOnce()).copyAll(folderPath);
    }

    /**
     * Applying dark theme for gallery page.
     */
    @Test
    public void shouldApplyDarkThemeForGalleryPage() throws Exception {
        this.mvc.perform(get(DARK_THEME))
                .andExpect(model().attribute("isDark", true));
    }

    /**
     * Displaying gallery page with pictures
     * in original resolution.
     */
    @Test
    public void shouldApplyOriginalResolutionOnPictures() throws Exception {
        this.mvc.perform(get(ORIGINAL))
                .andExpect(model().attribute("isOriginal", true));
    }
}
