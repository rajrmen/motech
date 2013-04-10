package org.motechproject.commons.api;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MotechFileUtilsTest {


    private File testDirectory;
    private File testSubDirectory;

    @Before
    public void before() {
        testDirectory = new File("test1");
        testDirectory.mkdir();
        testSubDirectory = new File(testDirectory.getAbsolutePath() + "/test2");
        testSubDirectory.mkdir();
    }

    @Test
    public void shouldRecursivelyListAllFiles() throws IOException {
        createTestFileInSubDirectory("xyz.properties");
        createTestFileInSubDirectory("xyz.test");
        List<File> files = MotechFileUtils.recursivelyListAllFiles(testDirectory);
        assertThat(files.size(), is(2));
        assertThat(files.get(0).getName(), is("xyz.properties"));
        assertThat(files.get(1).getName(), is("xyz.test"));
    }

    @Test
    public void shouldListFilesAccordingToFilter() throws IOException {
        createTestFileInSubDirectory("xyz.properties");
        createTestFileInSubDirectory("xyz.test");
        List<File> files = MotechFileUtils.recursivelyListAllFiles(testDirectory, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".test");
            }
        });
        assertThat(files.size(), is(1));
        assertThat(files.get(0).getName(), is("xyz.test"));
    }

    @Test
    public void shouldRecursivelyListAllPropertiesFiles() throws IOException {
        createTestFileInSubDirectory("xyz.properties");
        createTestFileInSubDirectory("xyz.test");
        List<File> files = MotechFileUtils.recursivelyListAllPropertyFiles(testDirectory);
        assertThat(files.size(), is(1));
        assertThat(files.get(0).getName(), is("xyz.properties"));
    }

    @After
    public void after() throws IOException {
        FileUtils.deleteDirectory(testDirectory);
    }

    private void createTestFileInSubDirectory(String fileName) throws IOException {
        File file = new File(String.format("%s/%s", testSubDirectory.getAbsolutePath(), fileName));
        file.createNewFile();
        assertTrue(file.exists());
    }
}
