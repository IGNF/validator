package fr.ign.validator.command;

import java.util.concurrent.TimeUnit;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * 
 * Display validator version
 * 
 * @author MBorne
 */
public class SleepCommand extends AbstractCommand {

    public static final String NAME = "sleep";

    private static final String OPT_DURATION = "duration";

    private Long duration = 3600L;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Sleep for a given duration (allows testing for process timeout and interruption)";
    }

    @Override
    public void execute() throws Exception {
        stdout.println("sleep for " + duration + " second(s)...");
        TimeUnit.SECONDS.sleep(duration);
        stdout.println("sleep for " + duration + " second(s) : completed");
    }

    @Override
    protected void buildCustomOptions(Options options) {
        {
            Option option = new Option(
                null, OPT_DURATION, true,
                "Sleep duration in seconds"
            );
            option.setArgName(OPT_DURATION);
            option.setRequired(true);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        this.duration = Long.valueOf(commandLine.getOptionValue(OPT_DURATION));
    }

}
