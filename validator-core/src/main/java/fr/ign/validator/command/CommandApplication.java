package fr.ign.validator.command;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import fr.ign.validator.Version;

/**
 *
 * Provide a system to have a single CLI application with multiple sub-command
 *
 * @author MBorne
 */
public class CommandApplication {

    private List<Command> commands = new ArrayList<>();

    /**
     * Standard output stream
     */
    protected PrintStream stdout = System.out;

    public CommandApplication() {
        loadRegistredCommands();
    }

    /**
     * Loads commands from
     * src/main/resources/META-INF/services/fr.ign.validation.command.Command
     */
    private void loadRegistredCommands() {
        ServiceLoader<Command> loader = ServiceLoader.load(Command.class);
        for (Command command : loader) {
            addCommand(command);
        }
    }

    /**
     * Add command
     *
     * @param command
     */
    public void addCommand(Command command) {
        commands.add(command);
    }

    /**
     * Get command by name
     *
     * @param name
     * @return
     */
    private Command getCommandByName(String name) {
        for (Command command : commands) {
            if (command.getName().equals(name)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Display global help
     */
    private void displayHelp() {
        stdout.println("Usage : COMMAND --help");
        stdout.println("");
        stdout.println("Validate, normalize and extract data according to models");
        stdout.println("");
        stdout.println("Version : " + Version.getVersion());
        stdout.println("");
        stdout.println("Commands:");
        stdout.println("");
        for (Command command : commands) {
            stdout.println(formatHelpItem(command.getName(), command.getDescription()));
        }
        stdout.println("");
        stdout.println("Environment variables:");
        stdout.println("");
        stdout.println(formatHelpItem("OGR2OGR_PATH", "Path to ogr2ogr executable from GDAL (default is 'ogr2ogr')"));
        stdout.println(
            formatHelpItem("OGR2OGR_TIMEOUT", "Maximum duration of an ogr2ogr conversion in seconds (default is 3600)")
        );
        stdout.println(
            formatHelpItem("GMLAS_CONFIG", "Path to a GMLAS driver configuration (XML validation of GML files)")
        );
        stdout.println(
            formatHelpItem(
                "DB_URL", "JDBC URL of a PostgreSQL validation database (default : SQLite in the output directory)"
            )
        );
        stdout.println(formatHelpItem("DB_USER, DB_PASSWORD", "Credentials of the PostgreSQL validation database"));
        stdout.println(
            formatHelpItem(
                "DB_SCHEMA", "PostgreSQL schema of the validation, dropped and created again (default is 'validation')"
            )
        );
        stdout.println(
            formatHelpItem(
                "DB_STATEMENT_TIMEOUT",
                "Maximum duration of a PostgreSQL query in seconds (default is 3600, 0 for no limit)"
            )
        );
        stdout.println(formatHelpItem("HTTP_PROXY", "ex : http://proxy:3128"));
        stdout.println(formatHelpItem("HTTPS_PROXY", "ex : http://proxy:3128"));
        stdout.println(formatHelpItem("NO_PROXY", "localhost,demo.localhost"));
        stdout.println("\t" + "");
    }

    /**
     * Format item for help.
     *
     * @param name
     * @param description
     * @return
     */
    private String formatHelpItem(String name, String description) {
        return "\t" + name + " - " + description;
    }

    /**
     * Run command
     *
     * @param args
     * @return
     */
    public int run(String args[]) {
        if (args.length == 0) {
            displayHelp();
            return 1;
        }
        String commandName = args[0];
        if (commandName.equals("--help") || commandName.equals("-h")) {
            displayHelp();
            return 0;
        }
        Command command = getCommandByName(commandName);
        if (command == null) {
            System.err.println("command '" + commandName + "' not found");
            displayHelp();
            return 1;
        }
        try {
            return command.run(Arrays.copyOfRange(args, 1, args.length));
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * Allows to replace writes to out.
     *
     * @param out
     */
    public void setStdout(PrintStream stdout) {
        this.stdout = stdout;
    }

}
