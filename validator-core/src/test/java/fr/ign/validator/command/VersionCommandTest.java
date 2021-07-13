package fr.ign.validator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Version;

public class VersionCommandTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDefaultOutput() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        VersionCommand version = new VersionCommand();
        version.setStdout(out);
        String[] args = {};
        assertEquals(0, version.run(args));
        assertEquals(
            Version.VERSION,
            StringUtils.trim(os.toString(StandardCharsets.UTF_8))
        );
    }

    @Test
    public void testOutputToFile() throws IOException {
        File targetFile = new File(folder.getRoot(), "version.txt");

        VersionCommand version = new VersionCommand();
        String[] args = {
            "--output", targetFile.getAbsolutePath()
        };
        assertEquals(0, version.run(args));

        assertTrue(targetFile.exists());
        List<String> lines = FileUtils.readLines(targetFile, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals(Version.VERSION, lines.get(0));

        // unsure no failure if file exists
        assertEquals(0, version.run(args));
    }

}
