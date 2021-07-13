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

public class CommandApplicationTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testHelp() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        CommandApplication application = new CommandApplication();
        application.setStdout(out);
        String[] args = {
            "--help"
        };

        assertEquals(0, application.run(args));
        String output = StringUtils.trim(os.toString(StandardCharsets.UTF_8));
        assertTrue("help should contains version", output.contains(Version.VERSION));
        assertTrue("help should display command list", output.contains("Commands:"));
        assertTrue("help should display env vars list", output.contains("Environment variables:"));
        assertTrue("help should display env var for ogr2ogr", output.contains("OGR2OGR_PATH"));
    }

    @Test
    public void testSubCommandVersion() throws IOException {
        File targetFile = new File(folder.getRoot(), "version.txt");

        CommandApplication application = new CommandApplication();
        String[] args = {
            "version",
            "--output", targetFile.getAbsolutePath()
        };

        assertEquals(0, application.run(args));
        List<String> lines = FileUtils.readLines(targetFile, StandardCharsets.UTF_8);
        assertEquals(1, lines.size());
        assertEquals(Version.VERSION, lines.get(0));
    }

}
