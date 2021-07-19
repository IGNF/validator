package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * Helper class to read data from {@link MultiTableFile}.
 * 
 * @author MBorne
 *
 */
public class MultiTableReader {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("MultiTableReader");

    /**
     * Extension given to the sub-directory containing CSV dumped by ogr2ogr
     */
    public static final String TMP_EXTENSION = "vtabs";

    /**
     * Directory containing CSV converted files (ex: path/to/multi.vtabs)
     */
    private File csvDirectory;

    /**
     * The name of the tables corresponding to a CSV file with the following path :
     * {csvDirectory}/{tableName}.csv
     */
    private List<String> tableNames;

    /**
     * Create a {@link MultiTableReader} with a source gmlPath and a csvDirectory
     * produced by an ogr2ogr conversion.
     * 
     * @param gmlPath
     * @param csvDirectory
     */
    private MultiTableReader(File gmlPath, File csvDirectory) {
        assert csvDirectory.exists();
        if (!csvDirectory.isDirectory()) {
            throw new RuntimeException("fail to read " + gmlPath + "(" + csvDirectory + " is not a directory)");
        }
        this.csvDirectory = csvDirectory;
        this.tableNames = this.retrieveTableNames();
    }

    /**
     * Retrieve table names listing files in converted folder.
     * 
     * @return
     */
    private List<String> retrieveTableNames() {
        List<String> result = new ArrayList<>();
        log.info(MARKER, "Retrieve table names...");
        for (File file : this.csvDirectory.listFiles()) {
            String filename = file.getName();
            if (!filename.endsWith(".csv")) {
                continue;
            }
            String tableName = FilenameUtils.getBaseName(filename);
            log.debug(MARKER, "Found table '{}'", tableName);
            result.add(tableName);
        }
        Collections.sort(result);
        return result;
    }

    /**
     * Get available table names.
     * 
     * @return
     */
    public List<String> getTableNames() {
        return tableNames;
    }

    /**
     * Get reader for a specific table.
     * 
     * @param tableName
     * @return
     * @throws IOException
     */
    public TableReader getTableReader(String tableName) throws IOException {
        File tablePath = getTablePath(tableName);
        return new TableReader(tablePath, StandardCharsets.UTF_8);
    }

    /**
     * Get path to the table converted to CSV.
     * 
     * @param tableName
     * @return
     * @throws IOException
     */
    public File getTablePath(String tableName) throws IOException {
        for (String candidate : tableNames) {
            if (!candidate.equalsIgnoreCase(tableName)) {
                continue;
            }
            return new File(csvDirectory, candidate + ".csv");
        }
        String message = String.format("Table '%1s' not found in '%2s'", tableName, csvDirectory);
        log.error(MARKER, message);
        throw new IOException(message);
    }

    /**
     * Create a multiple table reader converting source file to a CSV directory.
     * 
     * @see {@link fr.ign.validator.data.file.MultiTableFile.MultiTableFile#getReader()}
     * 
     * @param file
     * @param preferedCharset
     * @return
     * @throws IOException
     */
    public static MultiTableReader createMultiTableReader(File file, TableReaderOptions options) throws IOException {
        log.debug(
            MARKER, "Create MultiTableReader for '{}' (charset={},schema={})...",
            file.getAbsoluteFile(),
            options.getSourceCharset(),
            options.getXsdSchema()
        );

        /*
         * Get path to the directory where GML is convert into a set of CSV files
         */
        File csvDirectory = CompanionFileUtils.getCompanionFile(file, TMP_EXTENSION);
        if (!csvDirectory.exists()) {
            FileConverter.getInstance().convertToCSV(file, csvDirectory, options);
        }

        return new MultiTableReader(file, csvDirectory);
    }
}
