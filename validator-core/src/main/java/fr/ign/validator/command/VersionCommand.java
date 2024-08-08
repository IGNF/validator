package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

import fr.ign.validator.Version;
import fr.ign.validator.command.options.OutputFileOption;

/**
 *
 * Display validator version
 *
 * @author MBorne
 */
public class VersionCommand extends AbstractCommand {

    public static final String NAME = "version";

    private File outputFile = null;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Get validator version (ex : 4.1.0, 4.2.0-SNAPSHOT,...)";
    }

    @Override
    public void execute() throws Exception {
        PrintStream out = getOutputStream();
        out.println(Version.VERSION);
        out.close();
    }

    @Override
    protected void buildCustomOptions(Options options) {
        OutputFileOption.buildOptions(options);
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        this.outputFile = OutputFileOption.parseCustomOptions(commandLine);
    }

    private PrintStream getOutputStream() throws FileNotFoundException {
        if (outputFile == null) {
            return stdout;
        }
        if (outputFile.exists()) {
            FileUtils.deleteQuietly(outputFile);
        }
        return new PrintStream(outputFile);
    }

}
