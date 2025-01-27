package fr.ign.validator.cnig.command;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.process.DocumentGeometryProcess;
import fr.ign.validator.command.AbstractCommand;

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

    /**
     * Life cycle of command
     */
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

    /**
     * Processing data
     *
     * @param inputFiles
     * @param targetFile
     * @throws Exception
     */
    private void process(List<File> inputFiles, File targetFile) throws Exception {
        // Core processing
        DocumentGeometryProcess documentGeometryProcess = new DocumentGeometryProcess(inputFiles, geometryColumnNames);
        documentGeometryProcess.detectGeometries();
        String union = documentGeometryProcess.union();

        // Writing to file
        BufferedWriter bufferedWriter = getWriter(targetFile);
        bufferedWriter.write(union);
        bufferedWriter.close();
    }

    /**
     * File Writer helper
     *
     * @param targetFile
     * @return
     * @throws IOException
     */
    private BufferedWriter getWriter(File targetFile) throws IOException {
        if (targetFile.exists()) {
            targetFile.delete();
        }
        return new BufferedWriter(new FileWriter(targetFile));
    }

    /**
     * auxilliary options for commnad
     */
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

    /**
     * Parses command input from command line
     */
    @Override
    protected void parseCustomOptions(CommandLine commandLine) throws ParseException {
        String inputString = (String) commandLine.getParsedOptionValue("input");
        String geometryColumnNameString = (String) commandLine.getParsedOptionValue("geometries");
        this.outputFile = (File) commandLine.getParsedOptionValue("output");

        this.inputFiles = parseFileOption(inputString);
        this.geometryColumnNames = parseGeometryOption(geometryColumnNameString, commandLine.hasOption("geometries"));
    }


    /**
     * Asserts that Files input is conform
     *
     * @param inputString
     * @return
     * @throws ParseException
     */
    public List<File> parseFileOption(String inputString) throws ParseException{
        List<File> inputFiles = new ArrayList<File>();
        if (inputString.isEmpty()) {
            throw new ParseException("Input is empty");
        }
        String[] potentialStrings = inputString.split(", *");
        for (String potentialString : potentialStrings){
            File potentialFile = new File(potentialString);
            if (!potentialFile.isFile()){
                throw new ParseException(potentialString + " cannot be found");
            }
            if (FilenameUtils.getExtension(potentialString) != "csv"){
                throw new ParseException(potentialString + " must be a CSV file");
            }
            inputFiles.add(potentialFile);
        }
        return inputFiles;
    }

    /**
     * Asserts that geometry input is conform
     *
     * @param geometryColumnNameString
     * @param hasOption
     * @return
     */
    public List<String> parseGeometryOption(String geometryColumnNameString, boolean hasOption){
        List<String> geometryColumnNames = new ArrayList<String>();
        geometryColumnNames.add("geom");
        geometryColumnNames.add("geometry");
        if (hasOption) {
            String[] names = geometryColumnNameString.split(", *");
            for (String geometryName : names){
                geometryColumnNames.add(geometryName);
            }
        }
        return geometryColumnNames;
    }

}
