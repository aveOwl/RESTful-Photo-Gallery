package com.gallery.restful.controller;

import com.gallery.restful.service.PhotoService;
import com.gallery.restful.util.DirectoryScanner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.ArrayList;

import static com.gallery.restful.navigation.Points.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * {@code @WebMvcTest} based tests for {@link PhotoController}.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(PhotoController.class)
public class PhotoControllerTest {

    /**
     * Main entry point for server-side Spring MVC test support.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * Mocking {@link PhotoService} behaviour.
     */
    @MockBean
    private PhotoService photoService;

    /**
     * Creating and deleting temporary folder structure.
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Testing proper redirection to home page, when
     * root path is used.
     */
    @Test
    public void getRedirectedToHomePage() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(HOME));
    }

    /**
     * Testing default home page view, when accessed with GET request.
     */
    @Test
    public void getHomeDefaultModelWithGETRequest() throws Exception {
        this.mvc.perform(get(HOME))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("index"));
    }

    /**
     * Testing default gallery page view, without any pictures.
     */
    @Test
    public void getGalleryDefaultModelWithGETRequest() throws Exception {
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
     * Testing redirection from home page proceeding POST request
     * in order to upload files to gallery page.
     */
    @Test
    public void getRedirectedFromHomePostRequestToGalleryPage() throws Exception {
        String folderPath = temporaryFolder.newFolder().getAbsolutePath();

        this.mvc.perform(post(HOME).param("path", folderPath))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GALLERY));
        verify(photoService, atLeastOnce()).copyAll(folderPath);
    }

    /**
     * Testing dark theme.
     */
    @Test
    public void getDarkThemeGalleryGETRequest() throws Exception {
        this.mvc.perform(get(DARK_THEME))
                .andExpect(model().attribute("isDark", true));
    }

    /**
     * Testing original sized pictures property.
     */
    @Test
    public void getOriginalSizedPicturesGalleryGETRequest() throws Exception {
        this.mvc.perform(get(ORIGINAL))
                .andExpect(model().attribute("isOriginal", true));
    }
}
