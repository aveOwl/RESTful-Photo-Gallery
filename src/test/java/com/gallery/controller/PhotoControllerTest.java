package com.gallery.controller;

import com.gallery.service.PhotoService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * WebMvcTests for {@link PhotoController} class.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@Import(PhotoController.class)
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
    private MockMvc mvc;

    /**
     * A controller class to be tested.
     */
    @Autowired
    private PhotoController photoController;

    /**
     * A mock of {@link PhotoService} class.
     */
    @MockBean
    private PhotoService photoService;

    /**
     * A mock of {@link ResourceLoader} class.
     */
    @MockBean
    private ResourceLoader resourceLoader;

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
        this.mvc = standaloneSetup(photoController).build();
    }

    /**
     * Redirection from root path onto home page.
     * @throws Exception on error.
     */
    @Test
    public void shouldRedirect() throws Exception {
        this.mvc.perform(get("/"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(HOME_URI));
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
        final String path = tf.newFolder().getAbsolutePath();

        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GALLERY_URI));

        verify(photoService, only()).copyAll(path);
    }

    /**
     * Should not proceed field submission on non-existing
     * directory path.
     * Should render error page with exception message.
     * @throws Exception on error.
     */
    @Test
    public void shouldRenderErrorPage() throws Exception {
        final String path = "/smth";
        final String errorMsg = "test exception";

        doThrow(new IllegalArgumentException(errorMsg))
                .when(photoService).copyAll(path);

        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(errorMsg)));

        verify(photoService, only()).copyAll(path);
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
