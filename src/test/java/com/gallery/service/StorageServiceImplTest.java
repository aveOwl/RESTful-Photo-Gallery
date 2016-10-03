package com.gallery.service;

import com.gallery.util.StorageFileNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = StorageServiceImpl.class)
public class StorageServiceImplTest {
    private static final String TEST_FILE_NAME = "test-file";

    @Autowired
    private StorageService storageService;

    @MockBean
    private Resource resource;

    @Rule
    public TemporaryFolder tf = new TemporaryFolder();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldNotLoadFileAsResourceIfResourceNotExists() throws Exception {
        // given
        given(this.resource.exists())
                .willReturn(false);

        thrown.expect(StorageFileNotFoundException.class);
        thrown.expectMessage("Could not read file " + TEST_FILE_NAME);

        // when
        this.storageService.loadAsResource(TEST_FILE_NAME);
    }

    @Test
    public void shouldNotLoadFileAsResourceIfResourceIsNotReadable() throws Exception {
        // given
        given(this.resource.isReadable())
                .willReturn(false);

        thrown.expect(StorageFileNotFoundException.class);
        thrown.expectMessage("Could not read file " + TEST_FILE_NAME);

        // when
        this.storageService.loadAsResource(TEST_FILE_NAME);
    }
}
