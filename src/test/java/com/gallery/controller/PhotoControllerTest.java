package com.gallery.controller;

import com.gallery.service.DestroyService;
import com.gallery.service.InitService;
import com.gallery.service.StorageService;
import com.gallery.util.StorageException;
import com.gallery.util.StorageFileNotFoundException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringRunner.class)
@WebMvcTest(PhotoController.class)
public class PhotoControllerTest {
    private static final String HOME_URI = "/photo";
    private static final String GALLERY_URI = "/photo/gallery";
    private static final String ERROR_MSG = "test-error";
    @Rule
    public TemporaryFolder tf = new TemporaryFolder();
    @Autowired
    private MockMvc mvc;
    @Autowired
    private PhotoController photoController;
    @Autowired
    private PhotoControllerAdvice photoControllerAdvice;
    @MockBean
    private StorageService storageService;
    @MockBean
    private InitService initService;
    @MockBean
    private DestroyService destroyService;

    @Before
    public void setUp() throws Exception {
        // when
        this.mvc = standaloneSetup(photoController)
                .setControllerAdvice(photoControllerAdvice)
                .build();
    }

    @Test
    public void shouldRenderHomePage() throws Exception {
        // when
        this.mvc.perform(get(HOME_URI))
                .andExpect(status().isOk())
                .andExpect(model().attribute("home", true))
                .andExpect(model().hasNoErrors());
    }

    /**
     * TODO should test that links are being created as well.
     */
    @Test
    public void shouldUploadPicturesAndRedirectToGallery() throws Exception {
        // given
        String path = tf.newFolder().getAbsolutePath();
        Stream<Path> stream = Stream.of(Paths.get("file1"), Paths.get("file1"));

        given(storageService.loadAll())
                .willReturn(stream);

        // when
        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl(GALLERY_URI));

        // then
        verify(storageService, atLeastOnce()).save(Paths.get(path));
        verify(storageService, atLeastOnce()).loadAll();
    }

    @Test
    public void shouldFailToUploadPicturesAndRenderErrorPage() throws Exception {
        // given
        String path = tf.newFolder().getAbsolutePath();

        doThrow(new StorageException(ERROR_MSG))
                .when(storageService).save(Paths.get(path));

        // when
        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(ERROR_MSG)));

        // then
        verify(storageService, atLeastOnce()).save(Paths.get(path));
        verify(storageService, never()).loadAll();
    }

    @Test
    public void shouldFailToLoadNonExistingFileAndRenderErrorPage() throws Exception {
        // given
        String fileName = "test-file";

        given(storageService.loadAsResource(fileName))
                .willThrow(new StorageFileNotFoundException(ERROR_MSG));

        // when
        this.mvc.perform(get("/photo/gallery/picture/" + fileName))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(ERROR_MSG)));

        // then
        verify(storageService, atLeastOnce()).loadAsResource(fileName);
    }


    @Test
    public void shouldRenderErrorPageWithInternalServerErrorStatus() throws Exception {
        // given
        String path = "/smth";

        doThrow(new NullPointerException(ERROR_MSG))
                .when(storageService).save(Paths.get(path));

        // when
        this.mvc.perform(post(HOME_URI).param("path", path))
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("description", containsString(ERROR_MSG)));

        // then
        verify(storageService, atLeastOnce()).save(Paths.get(path));
        verify(storageService, never()).loadAll();
    }

    @Test
    public void shouldRenderGalleryPage() throws Exception {
        // when
        this.mvc.perform(get(GALLERY_URI))
                .andExpect(status().isOk())
                .andExpect(model().attribute("gallery", true))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldApplyDarkTheme() throws Exception {
        // when
        this.mvc.perform(get(GALLERY_URI + "/darkbackground"))
                .andExpect(model().attribute("isDark", true))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldApplyOriginalResolution() throws Exception {
        // when
        this.mvc.perform(get(GALLERY_URI + "/original"))
                .andExpect(model().attribute("isOriginal", true))
                .andExpect(model().hasNoErrors());
    }

    @Test
    public void shouldApplyResizing() throws Exception {
        // when
        this.mvc.perform(get(GALLERY_URI + "/wh/200x500"))
                .andExpect(model().attribute("width", "200"))
                .andExpect(model().attribute("height", "500"))
                .andExpect(model().hasNoErrors());
    }
}
