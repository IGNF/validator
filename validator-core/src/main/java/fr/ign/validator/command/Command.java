package fr.ign.validator.command;

import java.io.PrintStream;

import org.apache.commons.cli.Options;

/**
 *
 * Interface for CLI interpreter commands
 *
 * @author MBorne
 *
 */
public interface Command {

    /**
     * Get command identifier (ex : document_validator)
     *
     * @return
     */
    public String getName();

    /**
     * Get command description (ex : validate a document according to a model)
     *
     * @return
     */
    public String getDescription();

    /**
     * Get command line options for CLI interpreter
     *
     * @return
     */
    public Options getCommandLineOptions();

    /**
     * Run the command with CLI arguments (except command name) and returns exit
     * code
     *
     * @param args
     */
    public int run(String[] args);

    /**
     * Run the command with arguments given as member variables
     *
     * @return
     */
    public void execute() throws Exception;

    /**
     * Allows to replace writes to out.
     *
     * @param out
     */
    public void setStdout(PrintStream out);

}
