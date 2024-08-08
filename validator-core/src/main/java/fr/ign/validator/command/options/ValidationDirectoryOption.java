package fr.ign.validator.command.options;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.ValidatorFatalError;

/**
 * Add an optional "--output" option to configure validation directory (default
 * is "validation" in the parent directory of the document)
 *
 * @author MBorne
 *
 */
public class ValidationDirectoryOption {

    private static final String OPTION_NAME = "output";
    public static final String VALIDATION_DIRECTORY_NAME = "validation";

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("ValidationDirectory");

    private ValidationDirectoryOption() {
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
                "O", OPTION_NAME, true,
                "Output validation directory (default is 'validation' directory next to the 'input' directory)"
            );
            option.setArgName("VALIDATION_DIR");
            option.setType(File.class);
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse validationDirectory from command line and create if.
     *
     * @param commandLine
     * @return
     * @throws ParseException
     */
    public static File parseCommandLine(CommandLine commandLine, File documentPath) throws ParseException {
        File validationDirectory = null;

        if (commandLine.hasOption(OPTION_NAME)) {
            validationDirectory = (File) commandLine.getParsedOptionValue(OPTION_NAME);
            if (validationDirectory.getAbsolutePath().startsWith(documentPath.getAbsolutePath())) {
                throw new IllegalArgumentException("output validation directory is a child of the input directory");
            }
        } else {
            validationDirectory = new File(documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME);
        }
        prepareValidationDirectory(validationDirectory);
        return validationDirectory;
    }

    /**
     * Cleanup and prepare output directory.
     */
    private static void prepareValidationDirectory(File validationDirectory) {
        log.info(MARKER, "Prepare validation directory {}...", validationDirectory.getAbsolutePath());
        if (validationDirectory.exists()) {
            log.info(MARKER, "Remove directory {}...", validationDirectory.getAbsolutePath());
            try {
                FileUtils.deleteDirectory(validationDirectory);
            } catch (Exception e) {
                String message = String.format("Fail to delete directory : '%1s'", validationDirectory);
                log.error(MARKER, message);
                throw new ValidatorFatalError(message);
            }
        }
        log.info(MARKER, "Create directory {}...", validationDirectory.getAbsolutePath());
        validationDirectory.mkdirs();
    }
}
