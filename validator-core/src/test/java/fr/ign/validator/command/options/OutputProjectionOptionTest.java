package fr.ign.validator.command.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import fr.ign.validator.model.Projection;

public class OutputProjectionOptionTest {

    @Test
    public void testDefaultIsCRS84() throws ParseException {
        CommandLine commandLine = createCommandLine(new String[] {});
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertEquals(Projection.CODE_CRS84, result.getCode());
    }

    @Test
    public void testSameAsSourceIsNull() throws ParseException {
        CommandLine commandLine = createCommandLine(new String[] {
            "--output-projection",
            "same-as-source"
        });
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertNull(result);
    }

    @Test
    public void testLambert93() throws ParseException {
        CommandLine commandLine = createCommandLine(new String[] {
            "--output-projection",
            "EPSG:2154"
        });
        Projection result = OutputProjectionOption.parseCommandLine(commandLine);
        assertEquals("EPSG:2154", result.getCode());
    }

    @Test(expected = ParseException.class)
    public void testNotFound() throws ParseException {
        CommandLine commandLine = createCommandLine(new String[] {
            "--output-projection",
            "NOT:FOUND"
        });
        OutputProjectionOption.parseCommandLine(commandLine);
    }

    private CommandLine createCommandLine(String[] args) throws ParseException {
        Options options = new Options();
        OutputProjectionOption.buildOptions(options);
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = parser.parse(options, args);
        return commandLine;
    }

}
