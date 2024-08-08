package fr.ign.validator.command.options;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.Projection;

/**
 * Add "output-projection" to the command line.
 *
 * @author MBorne
 *
 */
public class OutputProjectionOption {

    private static final String OPTION_NAME = "output-projection";

    private static final String SAME_AS_SOURCE = "same-as-source";

    /**
     * Add "srs" option
     *
     * @param options
     */
    public static void buildOptions(Options options) {
        {
            Option option = new Option(
                null, OPTION_NAME, true,
                "output projection for the normalized data (default is 'CRS:84', use --output-projection'"
                    + SAME_AS_SOURCE + "' to disable reprojection)"
            );
            option.setRequired(false);
            options.addOption(option);
        }
    }

    /**
     * Parse output-projection from the command line.
     *
     * @param commandLine
     * @return
     * @return Projection or null if "source" is specified.
     */
    public static Projection parseCommandLine(CommandLine commandLine) throws ParseException {
        String code = commandLine.getOptionValue(OPTION_NAME, Projection.CODE_CRS84);
        if (code.equalsIgnoreCase(SAME_AS_SOURCE)) {
            return null;
        }

        ProjectionList projectionRepository = ProjectionList.getInstance();
        Projection projection = projectionRepository.findByCode(code);
        if (projection == null) {
            String message = String.format("Invalid projection code '%1s' given for --output-projection", code);
            throw new ParseException(message);
        }
        return projection;
    }
}
