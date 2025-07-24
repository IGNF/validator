package fr.ign.validator.cnig.sup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.sup.internal.FileLocator;
import fr.ign.validator.tools.TableReader;

/**
 * Helper class to add columns on GENERATEUR and ASSIETTE tables output data :
 *
 * <ul>
 * <li>"fichier" is reported from "acte_sup" table exploring relations</li>
 * <li>"nomsuplitt" is reported from "servitude" table exploring relations</li>
 * <li>"nomreg" is reported from "servitude" table exploring relations</li>
 * </ul>
 *
 * @author MBorne
 *
 */
public class AdditionalColumnsBuilder {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("AdditionalColumnsBuilder");

    enum FileType {
        GENERATEUR, ASSIETTE
    };

    /**
     * Database dedicated to explore relations between SUP tables.
     */
    private DatabaseSUP database;

    /**
     * directory in which temporary CSV files are created.
     */
    private File tempDirectory;

    /**
     *
     * @param tempDirectory directory in which temporary CSV files are created.
     * @param dataDirectory
     */
    public AdditionalColumnsBuilder(DatabaseSUP database, File tempDirectory) {
        this.database = database;
        this.tempDirectory = tempDirectory;
    }

    /**
     * Add columns to GENERATEUR_SUP and ASSIETTE_SUP files in dataDirectory.
     *
     * @throws Exception
     */
    public void addColumnsToGenerateurAndAssietteFiles(File dataDirectory) throws Exception {
        /*
         * Locate files in data directory (stop process if some are not found)
         */
        FileLocator fileLocator = new FileLocator(dataDirectory);

        /*
         * Add columns to GENERATEUR_SUP files
         */
        for (File generateurSupFile : fileLocator.findGenerateurSupFiles()) {
            addColumnsToFile(FileType.GENERATEUR, generateurSupFile);
        }

        /* add columns to ASSIETTE_SUP files */
        for (File assietteSupFile : fileLocator.findAssietteSupFiles()) {
            addColumnsToFile(FileType.ASSIETTE, assietteSupFile);
        }
    }

    /**
     * Ajout de la colonne "fichiers" au fichier generateursFile
     *
     * @param database
     * @param generateursFile
     * @throws IOException
     */
    private void addColumnsToFile(FileType fileType, File file) throws Exception {
        log.info(MARKER, "Add 'fichier', 'nomsuplitt' and 'nomreg' columns to {} ...", file);
        /*
         * lecture des métadonnées du fichier en entrée
         */
        TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
        String[] inputHeader = reader.getHeader();

        /* idAss or idGen */
        String idColumnName = getIdColumnName(fileType);
        int idColumnIndex = reader.findColumn(idColumnName);
        if (idColumnIndex < 0) {
            log.error(MARKER, "Impossible de trouver la colonne identifiant dans {}...", file);
            return;
        }

        /* create output file in a tmp directory */
        File newFile = new File(tempDirectory, file.getName());
        log.debug(MARKER, "Create file {} ...", newFile);
        BufferedWriter fileWriter = new BufferedWriter(
            new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8)
        );
        CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180);

        /* create output header adding "fichier","nomsuplitt","nomreg" */
        printer.printRecord(createOutputHeader(inputHeader));

        while (reader.hasNext()) {
            String[] inputRow = reader.next();

            String id = inputRow[idColumnIndex];

            /* retrieve "fichier" using joins */
            List<DatabaseSUP.ActeServitude> actes = getActesById(database, fileType, id);
            List<String> fichiers = database.getFichiers(actes);

            /* retrieve "nomsuplitt" using joins */
            List<DatabaseSUP.Servitude> servitudes = getServitudesById(database, fileType, id);
            List<String> nomSupLitts = database.getNomSupLitts(servitudes);
            /* retrive "nomreg" usin joins */
            List<String> nomRegs = database.getNomRegs(servitudes);

            printer.printRecord(
                createOutputRow(
                    inputRow,
                    fichiers,
                    nomSupLitts // ,
                    // nomRegs
                )
            );
        }

        printer.close();

        // replace file
        log.debug(MARKER, "rename {} to {}...", newFile, file);
        file.delete();
        newFile.renameTo(file);
    }

    /**
     * Create output header with new column names.
     *
     * @param inputHeader
     * @return
     */
    private List<String> createOutputHeader(String[] inputHeader) {
        List<String> outputHeader = new ArrayList<String>(Arrays.asList(inputHeader));
        outputHeader.add(DatabaseSUP.COLUMN_FICHIER);
        outputHeader.add(DatabaseSUP.COLUMN_NOMSUPLITT);
        // outputHeader.add(DatabaseSUP.COLUMN_NOMREG);
        return outputHeader;
    }

    /**
     * Create output row with new column values.
     *
     * @param inputRow
     * @param fichiers
     * @return
     */
    private List<String> createOutputRow(String[] inputRow, List<String> fichiers, List<String> nomSupLitts // ,
    // List<String> nomRegs
    ) {
        List<String> outputRow = new ArrayList<String>(Arrays.asList(inputRow));

        // COLUMN_FICHIER
        outputRow.add(concat(fichiers));
        // COLUMN_NOMSUPLITT
        outputRow.add(concat(nomSupLitts));
        // COLUMN NOMREG
        // outputRow.add(concat(nomRegs));

        return outputRow;
    }

    /**
     * Concatène la liste des fichiers avec des pipes "|"
     *
     * @param fichiers
     * @return
     */
    private String concat(List<String> fichiers) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fichiers.size(); i++) {
            if (i != 0) {
                builder.append('|');
            }
            builder.append(fichiers.get(i));
        }
        return builder.toString();
    }

    /**
     * Get ActeServitude for an assiette or generateur id.
     *
     * @param database
     * @param fileType
     * @param id
     * @return
     */
    private List<DatabaseSUP.ActeServitude> getActesById(
        DatabaseSUP database,
        FileType fileType,
        String id) {
        switch (fileType) {
        case GENERATEUR:
            return database.findActesByGenerateur(id);
        case ASSIETTE:
            return database.findActesByAssiette(id);
        default:
            throw new IllegalArgumentException("Unexpected fileType : " + fileType);
        }
    }

    /**
     * Get Servitude for an assiette or generateur id.
     *
     * @param database
     * @param fileType
     * @param id
     * @return
     */
    private List<DatabaseSUP.Servitude> getServitudesById(
        DatabaseSUP database,
        FileType fileType,
        String id) {
        switch (fileType) {
        case GENERATEUR:
            return database.findServitudesByGenerateur(id);
        case ASSIETTE:
            return database.findServitudesByAssiette(id);
        default:
            throw new IllegalArgumentException("Unexpected fileType : " + fileType);
        }
    }

    /**
     * Renvoie le nom de la colonne identifiant en fonction du type de fichier
     *
     * @param fileType
     * @return
     */
    private String getIdColumnName(FileType fileType) {
        switch (fileType) {
        case GENERATEUR:
            return DatabaseSUP.COLUMN_IDGEN;
        case ASSIETTE:
            return DatabaseSUP.COLUMN_IDASS;
        default:
            throw new IllegalArgumentException("Unexpected fileType : " + fileType);
        }
    }

}
