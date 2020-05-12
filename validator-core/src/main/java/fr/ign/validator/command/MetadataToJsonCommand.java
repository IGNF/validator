package fr.ign.validator.command;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;

/**
 * Convert ISO 19115 metadata to JSON
 * 
 * @author MBorne
 *
 */
public class MetadataToJsonCommand extends AbstractCommand {

    public static final String NAME = "metadata_to_json";

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("MetadataToJsonCommand");
    
    /**
     * Input metadata file (XML) or folder
     */
    private File inputFile;

    /**
     * Optional output file. Default is stdout for a single file 
     * and {inputFile}.json for each XML file in a directory
     */
    private File outputFile;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute() throws Exception {
        if (inputFile.isDirectory()) {
            String[] extensions = {
                "xml"
            };
            log.info(MARKER, "Processing XML files in {} ...", inputFile.getAbsolutePath());
            Collection<File> sourceFiles = FileUtils.listFiles(inputFile, extensions, true);
            for (File sourceFile : sourceFiles) {
                File targetFile = new File(sourceFile.getAbsolutePath()+".json");
                log.info(MARKER, "{} ...", sourceFile.getAbsolutePath());
                try {
                    convertFile(sourceFile,targetFile);
                }catch(Exception e) {
                    log.error(MARKER, "fail to convert {} ", sourceFile.getAbsolutePath());
                    e.printStackTrace(System.err);
                }
            }
            log.info(MARKER, "complete");
        } else {
            convertFile(inputFile, outputFile);
        }
    }

    private void convertFile(File sourceFile, File targetFile) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        Metadata metadata = MetadataISO19115.readFile(sourceFile);
        objectMapper.writeValue(getOutputStream(targetFile), metadata);
    }

    private PrintStream getOutputStream(File targetFile) throws FileNotFoundException {
        if (targetFile == null) {
            return System.out;
        }
        if (targetFile.exists()) {
            targetFile.delete();
        }
        return new PrintStream(targetFile);
    }

    @Override
    protected void buildCustomOptions(Options options) {
        // input
        {
            Option option = new Option("i", "input", true, "Input file (xml)");
            option.setRequired(true);
            option.setType(File.class);
            options.addOption(option);
        }
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
        inputFile = (File) commandLine.getParsedOptionValue("input");
        outputFile = (File) commandLine.getParsedOptionValue("output");
        if (inputFile.isDirectory() && outputFile != null) {
            throw new ParseException("input file is a directory, output can't be specified");
        }
    }

}
