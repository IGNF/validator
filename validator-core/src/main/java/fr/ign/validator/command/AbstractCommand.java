package fr.ign.validator.command;

import java.io.PrintStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import fr.ign.validator.tools.Networking;

/**
 *
 * Common command implementation and helpers
 *
 * @author MBorne
 *
 */
public abstract class AbstractCommand implements Command {

    /**
     * Standard output stream
     */
    protected PrintStream stdout = System.out;

    @Override
    public void setStdout(PrintStream stdout) {
        this.stdout = stdout;
    }

    /**
     * Append custom CLI options to default ones
     *
     * @param options
     */
    protected abstract void buildCustomOptions(Options options);

    /**
     * Parse custom CLI options to member variable
     *
     * @param commandLine
     * @throws ParseException
     */
    protected abstract void parseCustomOptions(CommandLine commandLine) throws ParseException;

    @Override
    public Options getCommandLineOptions() {
        Options options = this.getCommonOptions();
        buildCustomOptions(options);
        return options;
    }

    @Override
    public final int run(String[] args) {
        Options options = getCommandLineOptions();

        /*
         * parse command line options and handle command line options error
         */
        CommandLineParser parser = new DefaultParser();
        if (containsHelp(args)) {
            displayHelp(options);
            return 0;
        }
        try {
            CommandLine commandLine = parser.parse(options, args);
            configureNetworkingAndProxy(commandLine);
            parseCustomOptions(commandLine);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            displayHelp(options);
            return 1;
        }

        /*
         * run command and handle execution error
         */
        try {
            this.execute();
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * True if command args contains -h or --help.
     *
     * @param args
     * @return
     */
    private boolean containsHelp(String[] args) {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get common options
     *
     * @return
     */
    private Options getCommonOptions() {
        Options options = new Options();

        // help
        options.addOption("h", "help", false, "display help message");

        // proxy
        {
            Option option = new Option("p", "proxy", true, "Network proxy (ex : proxy.ign.fr:3128)");
            option.setRequired(false);
            options.addOption(option);
        }

        return options;
    }

    /**
     * Display help
     *
     * @param options
     */
    private void displayHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        PrintWriter pw = new PrintWriter(stdout);
        formatter.printHelp(pw, 120, getName(), null, options, 0, 0, null, false);
        pw.flush();
    }

    /**
     * Parse proxy option and define proxy
     *
     * @param commandLine
     */
    protected void configureNetworkingAndProxy(CommandLine commandLine) {
        String proxy = commandLine.getOptionValue("proxy", "");
        Networking.configureHttpClient(proxy);
    }

}
