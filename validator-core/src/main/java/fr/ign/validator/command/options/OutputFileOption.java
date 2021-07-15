package fr.ign.validator.command.options;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Add option to configure an optional option to a file with a default to
 * System.stdout.
 * 
 * @author MBorne
 *
 */
public class OutputFileOption {

    private static final String OPTION_NAME = "output";

    private OutputFileOption() {
        // disabled (static helpers)
    }

    /**
     * Add "output" option to command line.
     * 
     * @param options
     */
    public static void buildOptions(Options options) {
        {
            Option option = new Option(
                null, OPTION_NAME, true,
                "Output file (default is stdout)"
            );
            option.setType(File.class);
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse option from command line.
     * 
     * @param commandLine
     * @return
     * @throws ParseException
     */
    public static File parseCustomOptions(CommandLine commandLine) throws ParseException {
        if (!commandLine.hasOption(OPTION_NAME)) {
            return null;
        }
        return (File) commandLine.getParsedOptionValue(OPTION_NAME);
    }

}
