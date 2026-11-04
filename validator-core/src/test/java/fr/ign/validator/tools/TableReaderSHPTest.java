package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Test TableReader with ESRI Shapefiles
 *
 * @author mborne
 */
public class TableReaderSHPTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * The conversion must not remove or overwrite a CSV file provided with the same
     * name as the shapefile.
     */
    @Test
    public void testConversionKeepsCsvWithSameName() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/shp_utf8/PRESCRIPTION_PCT.shp");
        File directory = folder.newFolder("input");
        for (String extension : new String[] {
            "shp", "shx", "dbf", "prj"
        }) {
            File companion = CompanionFileUtils.getCompanionFile(source, extension);
            if (companion.exists()) {
                FileUtils.copyFileToDirectory(companion, directory);
            }
        }
        File shp = new File(directory, "PRESCRIPTION_PCT.shp");
        File csv = new File(directory, "PRESCRIPTION_PCT.csv");
        FileUtils.writeStringToFile(csv, "ID,NAME\n1,user data\n", StandardCharsets.UTF_8);

        try (TableReader reader = TableReader.createTableReader(shp, StandardCharsets.UTF_8)) {
            assertEquals("WKT", reader.getHeader()[0]);
        }
        assertTrue(csv.exists());
        assertEquals("ID,NAME\n1,user data\n", FileUtils.readFileToString(csv, StandardCharsets.UTF_8));
        // only the input files, the CSV and the converted file (.vrows)
        assertEquals(6, directory.listFiles().length);
    }

    @Test
    public void testReadShpLatin1() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/shp_latin1/PRESCRIPTION_PCT.shp");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
            // check header
            {
                String[] row = reader.getHeader();
                assertEquals(9, row.length);
                assertEquals("WKT", row[0]);
                assertEquals("LIBELLE", row[1]);
            }
            // check line 1
            {
                String[] row = reader.next();
                assertEquals(9, row.length);

                assertEquals("Bâtiment agricole", row[1]);// LIBELLE
            }

        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testReadShpUtf8() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/shp_utf8/PRESCRIPTION_PCT.shp");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
            // check header
            {
                String[] row = reader.getHeader();
                assertEquals(9, row.length);
                assertEquals("WKT", row[0]);
                assertEquals("LIBELLE", row[1]);
            }
            // check line 1
            {
                String[] row = reader.next();
                assertEquals(9, row.length);

                assertEquals("Bâtiment agricole", row[1]);// LIBELLE
            }

        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testReadDbfLatin1() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/dbf_latin1/ACTE_SUP.dbf");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
            // check header
            {
                String[] row = reader.getHeader();
                assertEquals(9, row.length);
                assertEquals("IdActe", row[0]);
                assertEquals("nomActe", row[1]);
            }
            // check line 1
            {
                String[] row = reader.next();
                assertEquals(9, row.length);

                assertEquals("Création", row[5]);// LIBELLE
            }

        } catch (IOException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testReadFileWithBadChars() {
        // read file where last character in NOMFIC column is not "printable"
        File file = ResourceHelper.getResourceFile(getClass(), "/dbf/SPECIAL_CHARS.DBF");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);

            String[] header = reader.getHeader();
            assertEquals(9, header.length);

            int indexNomFic = reader.findColumn("NOMFIC");
            assertTrue(indexNomFic >= 0);

            int count = 0;
            while (reader.hasNext()) {
                String[] row = reader.next();
                assertEquals(header.length, row.length);

                String nomfic = row[indexNomFic];
                char lastChar = nomfic.charAt(nomfic.length() - 1);
                assertEquals("\\u0092", toUnicode(lastChar));

                count++;
            }
            assertEquals(85, count);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    private static String toUnicode(char ch) {
        return String.format("\\u%04x", (int) ch);
    }

    /**
     * ogr2ogr csv conversion outputs a strange result :
     *
     * IDSUP, 75 26 85 84 83 82 81 80
     *
     * This test verifies that the null elements from header are ignored
     */
    public void testReadSingleColumn() {
        File file = ResourceHelper.getResourceFile(getClass(), "/dbf/SINGLE_COLUMN_BUG.dbf");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

            String[] header = reader.getHeader();

            assertEquals(1, header.length);
            assertEquals("IDSUP", header[0]);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
