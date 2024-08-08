package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class FileUtilsTest {

    @Test
    public void testListFilesAndDirs() {
        File directory = ResourceHelper.getResourceFile(getClass(), "/documents/commune-sample");
        String[] extensions = {
            "xml"
        };
        Collection<File> files = FileUtils.listFilesAndDirs(directory, extensions);
        // 2 XML files and 1 sub-directory
        assertEquals(3, files.size());
    }

}
