package fr.ign.validator.command.options;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ign.validator.geometry.GeometryComplexityThreshold;
import fr.ign.validator.geometry.GeometryThreshold;

/**
 *
 * @author cbouche
 *
 */
public class GeometryComplexityThresholdOption {

    private static final String OPTION_NAME = "cnig-complexity-tolerance";

    private static final String REGEX_TRESHHOLD = "\\[\\[(.*]*)], \\[(.*]*)]]";

    private GeometryComplexityThresholdOption() {
        // disabled
    }

    /**
     * Add "cnig-complexity-tolerance" option to command line.
     *
     * @param options
     */
    public static void buildOptions(Options options) {
        {
            Option option = new Option(
                null, OPTION_NAME, true,
                "List of threshold to survey geometry complexity : [[warnPoint, warnPart, warnRing, warnDensity, warnRingPoint], [errPoint, errRing, errPart, errDensity, errRingPoint]]"
            );
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
    public static GeometryComplexityThreshold parseCustomOptions(CommandLine commandLine) throws ParseException {

        if (!commandLine.hasOption(OPTION_NAME)) {
            return null;
        }

        String parsedOption = (String) commandLine.getParsedOptionValue(OPTION_NAME);

        Pattern regexp = Pattern.compile(REGEX_TRESHHOLD); // NOSONAR

        Matcher matcher = regexp.matcher(parsedOption);
        if (!matcher.find()) {
            String message = String.format(
                "Invalid threshold format, %1s given for %1s",
                parsedOption, OPTION_NAME
            );
            throw new ParseException(message);
        }

        MatchResult result = matcher.toMatchResult();

        Double[] warningParameters = parseParameters(result.group(1).split(","));
        Double[] errorParameters = parseParameters(result.group(2).split(","));

        if (warningParameters == null || errorParameters == null) {
            String message = String.format(
                "Invalid threshold format, %1s given for %1s",
                parsedOption, OPTION_NAME
            );
            throw new ParseException(message);
        }

        return new GeometryComplexityThreshold(
            new GeometryThreshold(
                warningParameters[0].intValue(), warningParameters[1].intValue(),
                warningParameters[2].intValue(), warningParameters[3],
                warningParameters[4].intValue()
            ),
            new GeometryThreshold(
                errorParameters[0].intValue(), errorParameters[1].intValue(),
                errorParameters[2].intValue(), errorParameters[3],
                errorParameters[4].intValue()
            )
        );
    }

    private static Double[] parseParameters(String[] params) {
        Double[] parameters = new Double[5];

        if (params.length != 5) {
            return null;
        }

        try {
            parameters[0] = Double.parseDouble(params[0].trim());
            parameters[1] = Double.parseDouble(params[1].trim());
            parameters[2] = Double.parseDouble(params[2].trim());
            parameters[3] = Double.parseDouble(params[3].trim());
            parameters[4] = Double.parseDouble(params[4].trim());
        } catch (NumberFormatException e) {
            return null;
        }

        return parameters;
    }

}
