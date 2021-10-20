package fr.ign.validator.command.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ValidationDirectoryOptionTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Options options;

    private CommandLineParser parser;

    @Before
    public void setUp() {
        options = new Options();
        ValidationDirectoryOption.buildOptions(options);
        parser = new DefaultParser();
    }

    @Test
    public void testDefaultBehavior() throws ParseException {
        String[] args = {};
        CommandLine commandLine = parser.parse(options, args);
        File documentPath = getFakeDocumentPath();
        File result = ValidationDirectoryOption.parseCommandLine(commandLine, documentPath);
        // name is "validation"
        assertEquals("validation", result.getName());
        // folder is located in parent directory of the documentPath
        assertEquals(folder.getRoot().getAbsolutePath(), result.getParentFile().getAbsolutePath());
        // folder is created
        assertTrue(result.exists());
    }

    @Test
    public void testCustomLocation() throws ParseException {
        File expectedOutputDir = new File(folder.getRoot(), "custom-validation");
        String[] args = {
            "--output", expectedOutputDir.getAbsolutePath()
        };
        CommandLine commandLine = parser.parse(options, args);
        File documentPath = getFakeDocumentPath();
        File result = ValidationDirectoryOption.parseCommandLine(commandLine, documentPath);
        assertEquals(expectedOutputDir.getAbsolutePath(), result.getAbsolutePath());
        assertTrue(result.exists());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testChildOfDocumentIsForbidden() throws ParseException {
        File documentPath = getFakeDocumentPath();
        File expectedOutputDir = new File(documentPath, "child-directory");
        String[] args = {
            "--output", expectedOutputDir.getAbsolutePath()
        };
        CommandLine commandLine = parser.parse(options, args);
        ValidationDirectoryOption.parseCommandLine(commandLine, documentPath);
    }

    public File getFakeDocumentPath() {
        return new File(folder.getRoot(), "document-path");
    }
}
