package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.command.options.OutputFileOption;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.model.Projection;

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
    public String getDescription() {
        return "Export supported projections (JSON).";
    }

    @Override
    protected void buildCustomOptions(Options options) {
        OutputFileOption.buildOptions(options);
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        outputFile = OutputFileOption.parseCustomOptions(commandLine);
    }

    @Override
    public void execute() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<Projection> projections = ProjectionList.getInstance().findAll();
        objectMapper.writeValue(getOutputStream(), projections);
    }

    private PrintStream getOutputStream() throws FileNotFoundException {
        if (outputFile == null) {
            return stdout;
        }
        if (outputFile.exists()) {
            outputFile.delete();
        }
        return new PrintStream(outputFile);
    }

}
