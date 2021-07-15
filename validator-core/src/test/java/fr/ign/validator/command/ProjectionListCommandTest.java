package fr.ign.validator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectionListCommandTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testOutputToFile() throws IOException {
        File targetFile = new File(folder.getRoot(), "projections.json");

        ProjectionListCommand version = new ProjectionListCommand();
        String[] args = {
            "--output", targetFile.getAbsolutePath()
        };
        assertEquals(0, version.run(args));

        assertTrue(targetFile.exists());
        String content = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
        assertTrue(content.contains("CRS:84"));
        assertTrue(content.contains("EPSG:2154"));
    }

}
