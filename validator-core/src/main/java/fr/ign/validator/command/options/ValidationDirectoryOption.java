package fr.ign.validator.command.options;

import java.io.File;
import java.nio.file.Path;

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
    private static final String OPTION_NO_OVERWRITE = "no-overwrite";
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
        {
            Option option = new Option(
                null, OPTION_NO_OVERWRITE, false,
                "Fail if the output validation directory already exists and is not empty (default is to remove it)"
            );
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
            if (isChildOf(validationDirectory, documentPath)) {
                throw new IllegalArgumentException("output validation directory is a child of the input directory");
            }
        } else {
            validationDirectory = new File(documentPath.getParentFile(), VALIDATION_DIRECTORY_NAME);
        }
        if (commandLine.hasOption(OPTION_NO_OVERWRITE) && isNotEmptyDirectory(validationDirectory)) {
            throw new IllegalArgumentException(
                String.format(
                    "output validation directory '%s' already exists and is not empty (--%s)",
                    validationDirectory.getAbsolutePath(),
                    OPTION_NO_OVERWRITE
                )
            );
        }
        prepareValidationDirectory(validationDirectory);
        return validationDirectory;
    }

    /**
     * Check if a directory is the given parent or one of its descendants. Path
     * elements are compared (not strings) so that "/data/ERP_SORTIE" is not
     * considered as a child of "/data/ERP".
     */
    private static boolean isChildOf(File directory, File parent) {
        Path directoryPath = directory.toPath().toAbsolutePath().normalize();
        Path parentPath = parent.toPath().toAbsolutePath().normalize();
        return directoryPath.startsWith(parentPath);
    }

    private static boolean isNotEmptyDirectory(File directory) {
        if (!directory.exists()) {
            return false;
        }
        String[] children = directory.list();
        return !directory.isDirectory() || children == null || children.length > 0;
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
