package com.gallery.controller;

import com.gallery.service.StorageService;
import com.gallery.util.StorageException;
import com.gallery.util.StorageFileNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * WebMvcTests for {@link PhotoController} class.
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class PhotoControllerTest {
    /**
     * Home page URI.
     */
    private static final String HOME_URI = "/photo";

    /**
     * Gallery page URI.
     */
    private static final String GALLERY_URI = "/photo/gallery";

    /**
     * A {@link MockMvc} instance.
     */
    @Autowired
    private MockMvc mvc;

    /**
     * A mock of {@link StorageService} interface.
     */
    @MockBean
    private StorageService storageService;

    /**
     * A rule for creating temporary file structure.
     */
    @Rule
    public TemporaryFolder tf = new TemporaryFolder();

    /**
     * Registering controller with minimum infrastructure.
     * @throws Exception on error.
     */
    @Before
    public void setUp() throws Exception {
        this.mvc = standaloneSetup(new PhotoController(storageService))
                .setControllerAdvice(new PhotoControllerAdvice())
                .build();
    }

    /**
     * Redirection from root path onto home page.
     * @throws Exception on error.
     */
    @Test
    public void shouldRedirect() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(HOME_URI))
                .andExpect(model().hasNoErrors());
    }

    /**
     * Should display home page without any errors.
     * @throws Exception on error.
     */
    @Test
    public void shouldRenderHomePage() throws Exception {
        this.mvc.perform(get(HOME_URI))
                .andExpect(status().isOk())
                .andExpect(model().attribute("home", true))
                .andExpect(model().hasNoErrors());
    }

    /**
     * Should proceed field submission without errors.
     * Should redirect to gallery page.
     * @throws Exception on error.
     */
    @Test
    public void shouldPreformSubmit() throws Exception {
        String path = tf.newFolder().getAbsolutePath();
        Stream<Path> stream = Stream.of();

        when(storageService.loadAll())
                .thenReturn(stream);

        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GALLERY_URI));

        verify(storageService, atLeastOnce()).save(path);
        verify(storageService, atLeastOnce()).loadAll();
    }

    /**
     * Should not proceed field submission. Should render
     * error view with status code 400 Bad Request.
     * @throws Exception on error.
     */
    @Test
    public void shouldFailToSubmitOnInvalidPath() throws Exception {
        String path = tf.newFolder().getAbsolutePath();
        String errorMsg = "test exception";

        when(storageService.loadAll())
                .thenThrow(new StorageException(errorMsg));

        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(errorMsg)));

        verify(storageService, atLeastOnce()).save(path);
        verify(storageService, atLeastOnce()).loadAll();
    }

    /**
     * Should render error view with exception message and
     * HTTP status code 404 Not Found.
     * @throws Exception on error.
     */
    @Test
    public void shouldRenderErrorPageWithNotFoundStatus() throws Exception {
        String fileName = "test-file";
        String errorMsg = "test exception";

        when(storageService.loadAsResource(fileName))
                .thenThrow(new StorageFileNotFoundException(errorMsg));

        this.mvc.perform(get("/photo/gallery/picture/" + fileName))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(errorMsg)));

        verify(storageService, atLeastOnce()).loadAsResource(fileName);
    }


    /**
     * Should render error view with exception message and
     * HTTP status code 500 Internal Server Error.
     * @throws Exception on error.
     */
    @Test
    public void shouldRenderErrorPageWithInternalServerErrorStatus() throws Exception {
        String path = "/smth";
        String errorMsg = "test exception";

        doThrow(new NullPointerException(errorMsg))
                .when(storageService).save(path);

        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(errorMsg)));

        verify(storageService, atLeastOnce()).save(path);
    }

    /**
     * Should display gallery page without any errors.
     * @throws Exception on error.
     */
    @Test
    public void shouldRenderGalleryPage() throws Exception {
        this.mvc.perform(get(GALLERY_URI))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gallery", true))
                .andExpect(model().hasNoErrors());
    }

    /**
     * Should change background of gallery page.
     * @throws Exception on error.
     */
    @Test
    public void shouldApplyDarkTheme() throws Exception {
        this.mvc.perform(get(GALLERY_URI + "/darkbackground"))
                .andExpect(model().attribute("isDark", true))
                .andExpect(model().hasNoErrors());
    }

    /**
     * Should change resolution of all pictures in gallery to its
     * original size.
     * @throws Exception on error.
     */
    @Test
    public void shouldApplyOriginalResolution() throws Exception {
        this.mvc.perform(get(GALLERY_URI + "/original"))
                .andExpect(model().attribute("isOriginal", true))
                .andExpect(model().hasNoErrors());
    }

    /**
     * Should change picture resolution from defaults to custom
     * provided width and height.
     * @throws Exception on error.
     */
    @Test
    public void shouldApplyResizing() throws Exception {
        this.mvc.perform(get(GALLERY_URI + "/wh/200x500"))
                .andExpect(model().attribute("width", "200"))
                .andExpect(model().attribute("height", "500"))
                .andExpect(model().hasNoErrors());
    }
}
