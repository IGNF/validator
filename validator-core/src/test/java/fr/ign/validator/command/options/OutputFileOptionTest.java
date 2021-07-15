package fr.ign.validator.command.options;

import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

/**
 * Test {@link OutputFileOption} helper.
 * 
 * @author MBorne
 *
 */
public class OutputFileOptionTest {

    private Options options;

    private CommandLineParser parser;

    @Before
    public void setUp() {
        options = new Options();
        OutputFileOption.buildOptions(options);
        parser = new DefaultParser();
    }

    @Test
    public void testDefaultIsNull() throws ParseException, IOException {
        String[] args = {};
        CommandLine commandLine = parser.parse(options, args);
        assertNull(OutputFileOption.parseCustomOptions(commandLine));
    }

}
