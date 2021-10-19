package fr.ign.validator.command.options;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ign.validator.geometry.GeometryComplexityThreshold;

/**
 * 
 * @author cbouche
 *
 */
public class GeometryComplexityThresholdOption {


	private static final String OPTION_NAME = "cnig-complexity-tolerance";


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
                "List of threshold to survey geometry complexity : [[point, ring, part, density], [point, ring, part, density]]"
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

        String regexpStr = "\\[\\[(.*]*)], \\[(.*]*)]]";
        Pattern regexp = Pattern.compile(regexpStr);

        Matcher matcher = regexp.matcher(parsedOption);
        if (matcher.find()) {
            MatchResult result = matcher.toMatchResult();

            String[] paramsWarning = result.group(2).split(",");
            int param1 = Integer.parseInt(paramsWarning[0]);
            int param2 = Integer.parseInt(paramsWarning[1]);
            int param3 = Integer.parseInt(paramsWarning[2]);
            double param4 = Double.parseDouble(paramsWarning[3]);

            String[] paramsError = result.group(3).split(",");
            int param5 = Integer.parseInt(paramsError[0]);
            int param6 = Integer.parseInt(paramsError[1]);
            int param7 = Integer.parseInt(paramsError[2]);
            double param8 = Double.parseDouble(paramsError[3]);

            return new GeometryComplexityThreshold(
            		param1, param2, param3, param4,
            		param5, param6, param7, param8
    		);

        }

        return new GeometryComplexityThreshold();
    }
    

}
