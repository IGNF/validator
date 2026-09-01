package fr.ign.validator.cnig.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.tools.TableReader;

/**
 * Enrich {@code DATA/TITRES_PIECES_ECRITES.csv} with an {@code ORIGINAL_PATH}
 * column.
 *
 * Values are relative paths from the {@code Pieces_ecrites} directory
 * (excluding {@code Pieces_ecrites} itself), resolved from the {@code FICHIER}
 * column.
 *
 * Example: {@code 1_Rapport_de_presentation/30014_rapport_20171013.pdf}
 */
public class TitresPiecesEcritesOriginalPathBuilder {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TitresPiecesEcritesOriginalPathBuilder");

    public static final String COLUMN_FICHIER = "FICHIER";
    public static final String COLUMN_ORIGINAL_PATH = "ORIGINAL_PATH";
    public static final String PIECES_ECRITES_DIRNAME = "Pieces_ecrites";
    public static final String TITRES_PIECES_ECRITES_FILENAME = "TITRES_PIECES_ECRITES.csv";

    private final File documentDirectory;
    private final File tempDirectory;

    public TitresPiecesEcritesOriginalPathBuilder(File documentDirectory, File tempDirectory) {
        this.documentDirectory = documentDirectory;
        this.tempDirectory = tempDirectory;
    }

    /**
     * Add or fill {@code ORIGINAL_PATH} on the normalized TITRES_PIECES_ECRITES CSV
     * in {@code dataDirectory}.
     *
     * @param dataDirectory validation DATA directory
     * @throws IOException
     */
    public void addOriginalPathColumn(File dataDirectory) throws IOException {
        File titresFile = new File(dataDirectory, TITRES_PIECES_ECRITES_FILENAME);
        if (!titresFile.exists()) {
            log.info(MARKER, "Skipped - {} not found", titresFile);
            return;
        }

        File piecesEcritesDirectory = new File(documentDirectory, PIECES_ECRITES_DIRNAME);
        if (!piecesEcritesDirectory.exists()) {
            log.warn(MARKER, "Skipped - {} not found", piecesEcritesDirectory);
            return;
        }

        log.info(MARKER, "Add '{}' column to {} ...", COLUMN_ORIGINAL_PATH, titresFile);
        enrichFile(titresFile, piecesEcritesDirectory);
        log.info(MARKER, "Add '{}' column to {} : completed", COLUMN_ORIGINAL_PATH, titresFile);
    }

    private void enrichFile(File titresFile, File piecesEcritesDirectory) throws IOException {
        TableReader reader = TableReader.createTableReader(titresFile, StandardCharsets.UTF_8);
        String[] inputHeader = reader.getHeader();

        int fichierIndex = reader.findColumn(COLUMN_FICHIER);
        if (fichierIndex < 0) {
            log.error(MARKER, "Column '{}' not found in {}", COLUMN_FICHIER, titresFile);
            return;
        }

        int originalPathIndex = reader.findColumn(COLUMN_ORIGINAL_PATH);
        boolean appendColumn = originalPathIndex < 0;

        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        File newFile = new File(tempDirectory, titresFile.getName());
        try (
            BufferedWriter fileWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(newFile), StandardCharsets.UTF_8)
            );
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180)) {
            printer.printRecord(createOutputHeader(inputHeader, appendColumn));

            while (reader.hasNext()) {
                String[] inputRow = reader.next();
                String fichier = fichierIndex < inputRow.length ? inputRow[fichierIndex] : null;
                String originalPath = resolveOriginalPath(piecesEcritesDirectory, fichier);
                printer.printRecord(createOutputRow(inputRow, originalPath, originalPathIndex, appendColumn));
            }
        }

        if (!titresFile.delete()) {
            log.warn(MARKER, "Fail to delete {}", titresFile);
        }
        if (!newFile.renameTo(titresFile)) {
            throw new IOException("Fail to replace " + titresFile + " with " + newFile);
        }
    }

    private List<String> createOutputHeader(String[] inputHeader, boolean appendColumn) {
        List<String> outputHeader = new ArrayList<>(Arrays.asList(inputHeader));
        if (appendColumn) {
            outputHeader.add(COLUMN_ORIGINAL_PATH);
        }
        return outputHeader;
    }

    private List<String> createOutputRow(
        String[] inputRow,
        String originalPath,
        int originalPathIndex,
        boolean appendColumn) {
        List<String> outputRow = new ArrayList<>(Arrays.asList(inputRow));
        if (appendColumn) {
            outputRow.add(originalPath);
        } else if (originalPathIndex >= 0 && originalPathIndex < outputRow.size()) {
            outputRow.set(originalPathIndex, originalPath);
        }
        return outputRow;
    }

    /**
     * Resolve relative path from {@code Pieces_ecrites} for a given filename.
     *
     * @param piecesEcritesDirectory Pieces_ecrites root
     * @param fichier                value of FICHIER (may include #fragment)
     * @return relative path with {@code /} separators, or empty string if not found
     */
    String resolveOriginalPath(File piecesEcritesDirectory, String fichier) {
        if (StringUtils.isBlank(fichier)) {
            return "";
        }
        String filename = filterFragment(fichier.trim());
        File matched = findFileByFilename(piecesEcritesDirectory, filename);
        if (matched == null) {
            log.warn(
                MARKER,
                "File '{}' referenced by {} not found under {}",
                filename,
                COLUMN_FICHIER,
                piecesEcritesDirectory
            );
            return "";
        }
        Path relative = piecesEcritesDirectory.toPath().toAbsolutePath().relativize(
            matched.toPath().toAbsolutePath()
        );
        return relative.toString().replace('\\', '/');
    }

    private File findFileByFilename(File root, String filename) {
        Collection<File> files = FileUtils.listFiles(root, null, true);
        for (File file : files) {
            if (file.getName().equals(filename)) {
                return file;
            }
        }
        return null;
    }

    private String filterFragment(String path) {
        int position = path.lastIndexOf('#');
        if (position >= 0) {
            return path.substring(0, position);
        }
        return path;
    }

}
