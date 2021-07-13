package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.data.file.MultiTableFile;

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
     * Directory containing CSV converted files (ex: (ex : path/to/multi.vtabs)
     */
    private File csvDirectory;

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
    }

    /**
     * Get available table names.
     * 
     * @return
     */
    public List<String> getTableNames() {
        List<String> tableNames = new ArrayList<>();
        for (File file : this.csvDirectory.listFiles()) {
            String filename = file.getName();
            if (!filename.endsWith("csv")) {
                continue;
            }
            tableNames.add(FilenameUtils.removeExtension(filename));
        }
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
     */
    public File getTablePath(String tableName) {
        return new File(csvDirectory, tableName + ".csv");
    }

    /**
     * Create a multiple table reader converting
     * 
     * @param file
     * @param preferedCharset
     * @return
     * @throws IOException
     */
    public static MultiTableReader createMultiTableReader(File file) throws IOException {
        log.debug(
            MARKER, "Create MultiTableReader for '{}' (charset={})...",
            file.getAbsoluteFile(),
            StandardCharsets.UTF_8
        );

        /*
         * Get path to the directory where GML is convert into a set of CSV files
         */
        File csvDirectory = CompanionFileUtils.getCompanionFile(file, TMP_EXTENSION);
        if (!csvDirectory.exists()) {
            FileConverter.getInstance().convertToCSV(file, csvDirectory, StandardCharsets.UTF_8);
        }

        return new MultiTableReader(file, csvDirectory);
    }
}
