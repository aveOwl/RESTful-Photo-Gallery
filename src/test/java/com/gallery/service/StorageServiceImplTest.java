package com.gallery.service;

import com.gallery.util.StorageException;
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

import java.nio.file.Path;

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
        given(resource.exists())
                .willReturn(false);

        thrown.expect(StorageFileNotFoundException.class);
        thrown.expectMessage("Could not read file " + TEST_FILE_NAME);

        // when
        storageService.loadAsResource(TEST_FILE_NAME);
    }

    @Test
    public void shouldNotLoadFileAsResourceIfResourceIsNotReadable() throws Exception {
        // given
        given(resource.isReadable())
                .willReturn(false);

        thrown.expect(StorageFileNotFoundException.class);
        thrown.expectMessage("Could not read file " + TEST_FILE_NAME);

        // when
        storageService.loadAsResource(TEST_FILE_NAME);
    }

    @Test
    public void shouldNotSaveWhenNoDirectoryProvided() throws Exception {
        // given
        final Path testPath = tf.newFile().toPath();

        thrown.expect(StorageException.class);
        thrown.expectMessage("Inputted path does not point to any existing directory.");

        // when
        storageService.save(testPath);
    }

    @Test
    public void shouldNotSaveWhenAnEmptyDirectoryProvided() throws Exception {
        // given
        final Path testPath = tf.newFolder().toPath();

        thrown.expect(StorageException.class);
        thrown.expectMessage("Failed to store files. No files provided.");

        // when
        storageService.save(testPath);
    }

    @Test
    public void shouldNotSaveWhenNullPassed() throws Exception {
        // given
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Source directory can't be null.");

        // when
        storageService.save(null);
    }
}
