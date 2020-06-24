package fr.ign.validator.command;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ign.validator.Version;

/**
 * 
 * Display validator version
 * 
 * @author MBorne
 */
public class VersionCommand extends AbstractCommand {

    public static final String NAME = "version";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute() throws Exception {
        System.out.println(Version.VERSION);
    }

    @Override
    protected void buildCustomOptions(Options options) {

    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {

    }

}
