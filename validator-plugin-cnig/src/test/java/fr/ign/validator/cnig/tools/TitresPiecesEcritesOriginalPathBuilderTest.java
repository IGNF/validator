package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.tools.TableReader;

public class TitresPiecesEcritesOriginalPathBuilderTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testAddOriginalPathColumn() throws Exception {
        File documentDirectory = folder.newFolder("document");
        File piecesEcrites = new File(documentDirectory, "Pieces_ecrites");
        File rapportDir = new File(piecesEcrites, "1_Rapport_de_presentation");
        File annexesDir = new File(piecesEcrites, "4_Annexes");
        assertTrue(rapportDir.mkdirs());
        assertTrue(annexesDir.mkdirs());

        File rapportPdf = new File(rapportDir, "30014_rapport_20171013.pdf");
        Files.write(rapportPdf.toPath(), "dummy".getBytes(StandardCharsets.UTF_8));
        File reglementPdf = new File(annexesDir, "30014_reglement_20171013.pdf");
        Files.write(reglementPdf.toPath(), "dummy".getBytes(StandardCharsets.UTF_8));

        File dataDirectory = folder.newFolder("DATA");
        File titresFile = new File(dataDirectory, "TITRES_PIECES_ECRITES.csv");
        try (
            CSVPrinter printer = new CSVPrinter(
                new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(titresFile), StandardCharsets.UTF_8)
                ),
                CSVFormat.RFC4180
            )) {
            printer.printRecord("FICHIER", "TITRE");
            printer.printRecord("30014_rapport_20171013.pdf", "Rapport");
            printer.printRecord("30014_reglement_20171013.pdf#page=2", "Reglement");
            printer.printRecord("missing.pdf", "Missing");
        }

        File tempDirectory = folder.newFolder("tmp");
        TitresPiecesEcritesOriginalPathBuilder builder = new TitresPiecesEcritesOriginalPathBuilder(
            documentDirectory,
            tempDirectory
        );
        builder.addOriginalPathColumn(dataDirectory);

        TableReader reader = TableReader.createTableReader(titresFile, StandardCharsets.UTF_8);
        int fichierIndex = reader.findColumn("FICHIER");
        int originalPathIndex = reader.findColumn("ORIGINAL_PATH");
        assertTrue(fichierIndex >= 0);
        assertTrue(originalPathIndex >= 0);

        assertTrue(reader.hasNext());
        String[] row1 = reader.next();
        assertEquals("30014_rapport_20171013.pdf", row1[fichierIndex]);
        assertEquals("1_Rapport_de_presentation/30014_rapport_20171013.pdf", row1[originalPathIndex]);

        assertTrue(reader.hasNext());
        String[] row2 = reader.next();
        assertEquals("30014_reglement_20171013.pdf#page=2", row2[fichierIndex]);
        assertEquals("4_Annexes/30014_reglement_20171013.pdf", row2[originalPathIndex]);

        assertTrue(reader.hasNext());
        String[] row3 = reader.next();
        assertEquals("missing.pdf", row3[fichierIndex]);
        assertTrue(row3[originalPathIndex] == null || row3[originalPathIndex].isEmpty());
    }

    @Test
    public void testFillExistingOriginalPathColumn() throws Exception {
        File documentDirectory = folder.newFolder("document");
        File piecesEcrites = new File(documentDirectory, "Pieces_ecrites");
        File paddDir = new File(piecesEcrites, "2_PADD");
        assertTrue(paddDir.mkdirs());
        File paddPdf = new File(paddDir, "30014_padd_20171013.pdf");
        Files.write(paddPdf.toPath(), "dummy".getBytes(StandardCharsets.UTF_8));

        File dataDirectory = folder.newFolder("DATA");
        File titresFile = new File(dataDirectory, "TITRES_PIECES_ECRITES.csv");
        try (
            CSVPrinter printer = new CSVPrinter(
                new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(titresFile), StandardCharsets.UTF_8)
                ),
                CSVFormat.RFC4180
            )) {
            printer.printRecord("FICHIER", "TITRE", "ORIGINAL_PATH");
            printer.printRecord("30014_padd_20171013.pdf", "PADD", "");
        }

        File tempDirectory = folder.newFolder("tmp");
        TitresPiecesEcritesOriginalPathBuilder builder = new TitresPiecesEcritesOriginalPathBuilder(
            documentDirectory,
            tempDirectory
        );
        builder.addOriginalPathColumn(dataDirectory);

        TableReader reader = TableReader.createTableReader(titresFile, StandardCharsets.UTF_8);
        int originalPathIndex = reader.findColumn("ORIGINAL_PATH");
        assertTrue(reader.hasNext());
        String[] row = reader.next();
        assertEquals("2_PADD/30014_padd_20171013.pdf", row[originalPathIndex]);
    }

}
