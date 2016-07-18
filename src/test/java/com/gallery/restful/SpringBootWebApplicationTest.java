package com.gallery.restful;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Testing spring application launch.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SpringBootWebApplicationTest {

    /**
     * Creating and deleting temporary folder
     * structure.
     */
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldDeleteServerDirectory() throws Exception {
        assertTrue(FileSystemUtils.deleteRecursively(temporaryFolder.newFile()));
    }
}
