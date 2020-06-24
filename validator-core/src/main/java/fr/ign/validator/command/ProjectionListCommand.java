package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.model.Projection;
import fr.ign.validator.repository.ProjectionRepository;

/**
 * 
 * Exports supported projections
 * 
 * @author MBorne
 *
 */
public class ProjectionListCommand extends AbstractCommand {

    public static final String NAME = "projection_list";

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("ProjectionListCommand");

    private File outputFile;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void buildCustomOptions(Options options) {
        // output
        {
            Option option = new Option("o", "output", true, "Output file (json)");
            option.setRequired(false);
            option.setType(File.class);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        outputFile = (File) commandLine.getParsedOptionValue("output");
    }

    @Override
    public void execute() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Projection> projections = ProjectionRepository.getInstance().findAll();
        objectMapper.writeValue(getOutputStream(), projections);
    }

    private PrintStream getOutputStream() throws FileNotFoundException {
        if (outputFile == null) {
            return System.out;
        }
        if (outputFile.exists()) {
            outputFile.delete();
        }
        return new PrintStream(outputFile);
    }

}
