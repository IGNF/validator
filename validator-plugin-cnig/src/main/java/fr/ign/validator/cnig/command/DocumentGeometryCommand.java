package fr.ign.validator.cnig.command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.cnig.geometry.GeometryHelpers;
import fr.ign.validator.cnig.process.DocumentGeometryProcess;
import fr.ign.validator.cnig.tools.CSV;
import fr.ign.validator.command.AbstractCommand;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;

/**
 * Generates Document Geomtry from csv files
 *
 * @author DDarras
 *
 */
public class DocumentGeometryCommand extends AbstractCommand {

    public static final String NAME = "document-geometry";

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocumentGeometryCommand");
    public static final String COMMA_DELIMITER = ",";

    /**
     * Input string
     */
    private String inputString;

    /**
     * Geometry columns names string
     */
    private String geometryColumnNameString;

    /**
     * Input files
     */
    private List<File> inputFiles = new ArrayList<File>();

    /**
     *  Geometry columns names
     */
    private List<String> geometryColumnNames = new ArrayList<String>();

    /**
     * Output file
     */
    private File outputFile;

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDescription() {
        return "Generates Document Geometry from csv files";
    }

    @Override
    public void execute() throws Exception {
        File targetFile = new File(outputFile.getAbsolutePath());
        log.info(MARKER, "Processing ...");
        try {
            process(inputFiles, targetFile);
        } catch (Exception e) {
            log.error(MARKER, "Failure during processing");
            e.printStackTrace(System.err);
        }
        log.info(MARKER, "complete");
    }

    private void process(List<File> inputFiles, File targetFile) throws Exception {
        DocumentGeometryProcess documentGeometryProcess = new DocumentGeometryProcess(inputFiles, geometryColumnNames);
        documentGeometryProcess.detectGeometries();
        String union = documentGeometryProcess.union();
    }

    private BufferedWriter getWriter(File targetFile) throws IOException {
        if (targetFile.exists()) {
            targetFile.delete();
        }
        return new BufferedWriter(new FileWriter(targetFile));
    }

    @Override
    protected void buildCustomOptions(Options options) {
        // input
        {
            Option option = new Option("i", "input", true,
                "Input files (csv). Different inputs should be separated by ','");
            option.setRequired(true);
            option.setType(String.class);
            options.addOption(option);
        }
        {
            Option option = new Option("g", "geometries", true,
            "Input names of geometry columns names. Different names should be separated by ','. Default is 'geom, geometry'");
            option.setRequired(false);
            option.setType(String.class);
            options.addOption(option);
        }

        // output
        {
            Option option = new Option("o", "output", true, "Output file (csv)");
            option.setRequired(true);
            option.setType(File.class);
            options.addOption(option);
        }
    }

    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        this.inputString = (String) commandLine.getParsedOptionValue("input");
        this.geometryColumnNameString = (String) commandLine.getParsedOptionValue("geometries");
        this.outputFile = (File) commandLine.getParsedOptionValue("output");

        // input files parser
        if (this.inputString.isEmpty()) {
            throw new ParseException("Input is empty");
        }
        String[] potentialStrings = this.inputString.split(", *");
        for (String potentialString : potentialStrings){
            File potentialFile = new File(potentialString);
            if (!potentialFile.isFile()){
                throw new ParseException(potentialString + " cannot be found");
            }
            if (FilenameUtils.getExtension(potentialString) != "csv"){
                throw new ParseException(potentialString + " must be a CSV file");
            }
            this.inputFiles.add(potentialFile);
        }

        // input geometry columns names parser
        this.geometryColumnNames.add("geom");
        this.geometryColumnNames.add("geometry");
        if (commandLine.hasOption("geometries")){
            String[] names = this.geometryColumnNameString.split(", *");
            for (String geometryName : names){
                this.geometryColumnNames.add(geometryName);
            }
        }
    }

}
