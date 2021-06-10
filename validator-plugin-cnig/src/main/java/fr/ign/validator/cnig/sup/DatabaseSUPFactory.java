package fr.ign.validator.cnig.sup;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.sup.internal.FileLocator;

/**
 * Create DatabaseSUP instances
 * 
 * @author MBorne
 *
 */
public class DatabaseSUPFactory {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DatabaseSUPFactory");

    private File tempDirectory;

    public DatabaseSUPFactory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * Create DatabaseSUP from CSV files in dataDirectory.
     * 
     * @param dataDirectory
     * @return
     */
    public DatabaseSUP createFromDataDirectory(File dataDirectory) {
        /*
         * Locate files in data directory (stop process if some are not found)
         */
        FileLocator fileLocator = new FileLocator(dataDirectory);
        File servitudeFile = fileLocator.findServitudeFile();
        if (servitudeFile == null) {
            log.error(MARKER, "SERVITUDE file not found!");
            return null;
        }

        File acteSupFile = fileLocator.findActeSupFile();
        if (acteSupFile == null) {
            log.error(MARKER, "ACTE_SUP file not found!");
            return null;
        }

        File servitudeActeSupFile = fileLocator.findServitudeActeSupFile();
        if (servitudeActeSupFile == null) {
            log.error(MARKER, "SERVITUDE_ACTE_SUP file not found!");
            return null;
        }

        List<File> generateurSupFiles = fileLocator.findGenerateurSupFiles();
        if (generateurSupFiles.isEmpty()) {
            log.error(MARKER, "GENERATEUR_SUP file not found!");
            return null;
        }

        List<File> assietteSupFiles = fileLocator.findAssietteSupFiles();
        if (assietteSupFiles.isEmpty()) {
            log.error(MARKER, "ASSIETTE_SUP file not found!");
            return null;
        }

        /*
         * Create a DatabaseJointureSUP dedicated to explore relations
         */
        log.info(MARKER, "Create temp directory {}...", tempDirectory);
        tempDirectory.mkdir();

        try {
            DatabaseSUP database = new DatabaseSUP(tempDirectory);
            database.loadTableServitude(servitudeFile);
            database.loadTableActe(acteSupFile);
            database.loadTableServitudeActe(servitudeActeSupFile);
            for (File generateurSupFile : generateurSupFiles) {
                database.loadTableGenerateur(generateurSupFile);
            }
            for (File assietteSupFile : assietteSupFiles) {
                database.loadTableAssiette(assietteSupFile);
            }
            return database;
        } catch (Exception e) {
            log.error(MARKER, "Fail to create DatabaseSUP", e);
            return null;
        }
    }

}
