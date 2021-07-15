package fr.ign.validator.command.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.model.Projection;

/**
 * Test {@link OutputProjectionOption} helper.
 * 
 * @author MBorne
 *
 */
public class OutputProjectionOptionTest {

    private Options options;

    private CommandLineParser parser;

    @Before
    public void setUp() {
        options = new Options();
        OutputProjectionOption.buildOptions(options);
        parser = new DefaultParser();
    }

    @Test
    public void testDefaultIsCRS84() throws ParseException {
        String[] args = {};
        CommandLine commandLine = parser.parse(options, args);
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertEquals(Projection.CODE_CRS84, result.getCode());
    }

    @Test
    public void testSameAsSourceIsNull() throws ParseException {
        String[] args = {
            "--output-projection",
            "same-as-source"
        };
        CommandLine commandLine = parser.parse(options, args);
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertNull(result);
    }

    @Test
    public void testLambert93() throws ParseException {
        String[] args = {
            "--output-projection",
            "EPSG:2154"
        };
        CommandLine commandLine = parser.parse(options, args);
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertEquals("EPSG:2154", result.getCode());
    }

    @Test(expected = ParseException.class)
    public void testNotFound() throws ParseException {
        String[] args = {
            "--output-projection",
            "NOT:FOUND"
        };
        CommandLine commandLine = parser.parse(options, args);
        OutputProjectionOption.parseCommandLine(commandLine);
    }

}
