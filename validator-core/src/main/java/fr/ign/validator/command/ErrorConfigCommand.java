package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Collection;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.Context;
import fr.ign.validator.error.ValidatorError;

/**
 * Dump default configuration for error codes.
 * 
 * @author MBorne
 *
 */
public class ErrorConfigCommand extends AbstractCommand {

    public static final String NAME = "error_config";

    private File outputFile;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void buildCustomOptions(Options options) {
        // output
        {
            Option option = new Option("O", "output", true, "Output CSV file");
            option.setRequired(false);
            option.setType(File.class);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        if (commandLine.hasOption("output")) {
            this.outputFile = (File) commandLine.getParsedOptionValue("output");
        }
    }

    @Override
    public void execute() throws Exception {
        Context context = new Context();
        Collection<ValidatorError> prototypes = context.getErrorFactory().getPrototypes();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(getOutputStream(), prototypes);
    }

    private PrintStream getOutputStream() throws FileNotFoundException {
        if (this.outputFile == null) {
            return System.out;
        }
        if (this.outputFile.exists()) {
            this.outputFile.delete();
        }
        return new PrintStream(this.outputFile);
    }
}
