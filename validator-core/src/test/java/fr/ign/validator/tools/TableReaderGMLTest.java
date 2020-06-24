package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import fr.ign.validator.exception.InvalidCharsetException;

/**
 * 
 * Test CSV file reading with TableReader
 * 
 * @author mickael
 *
 */
public class TableReaderGMLTest {

    @Test
    public void testReadDocUrbaComGML() {
        File file = ResourceHelper.getResourceFile(getClass(), "/gml/DOC_URBA_COM.gml");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

            String[] header = reader.getHeader();
            assertTrue(header.length >= 4);

            int wktIndex = reader.findColumn("WKT");
            assertTrue("WKT column not found", wktIndex != 1);
            assertTrue(Arrays.asList(header).contains("gml_id"));
            assertTrue(Arrays.asList(header).contains("IDURBA"));
            assertTrue(Arrays.asList(header).contains("INSEE"));

            assertEquals(2, reader.findColumn("IDURBA"));

            // Note that DATECOG is ignored (gml is not fixed)
            // assertTrue(Arrays.asList(header).contains("DATECOG")) ;

            String[] row = reader.next();
            // WKT (regression in ogr2ogr between 1.x and 2.x)
            if (FileConverter.getInstance().getVersion().getFullVersion().startsWith("GDAL 2.")) {
                assertEquals("POINT (225499.742202533 6755725.59042703)", row[wktIndex]);
            } else {
                assertEquals("POINT (225499.742202532826923 6755725.590427031740546)", row[wktIndex]);
            }

            assertTrue(Arrays.asList(row).contains("DOC_URBA_COM.13")); // gml_id
            assertTrue(Arrays.asList(row).contains("5611820140612")); // IDURBA
            assertTrue(Arrays.asList(row).contains("56118")); // INSEE

            assertFalse(reader.hasNext());

        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InvalidCharsetException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testReadPrescriptionSurfGML() {
        File file = ResourceHelper.getResourceFile(getClass(), "/gml/PRESCRIPTION_SURF.gml");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

            String[] header = reader.getHeader();

            assertTrue(reader.findColumn("TXT") >= 0);
            assertTrue(reader.findColumn("WKT") >= 0);
            assertTrue(reader.findColumn("TYPEPSC") >= 0);
            assertTrue(reader.findColumn("TYPEPSC2") >= 0);

            assertTrue(reader.findColumn("URLFIC") >= 0); // always empty not removed

            assertTrue(reader.hasNext());
            String[] row = reader.next();
            assertEquals(header.length, row.length);

            // check that 05 is not converted to 5
            assertTrue(Arrays.asList(row).contains("05")); // TYPEPSC
            assertTrue(Arrays.asList(row).contains("20140123"));
        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InvalidCharsetException e) {
            fail(e.getMessage());
        }
    }

    /**
     * This test works with some ogr2ogr versions TODO NOMFIC should appears with
     * FixGML
     */
    @Test
    @Ignore
    public void testReadZoneUrbaGML() {
        File file = new File(getClass().getResource("/gml/ZONE_URBA.gml").getPath());
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

            String[] header = reader.getHeader();

            assertTrue(Arrays.asList(header).contains("LIBELLE"));
            // TODO NOMFIC should appears with FixGML
            assertTrue(Arrays.asList(header).contains("NOMFIC"));

        } catch (IOException e) {
            fail(e.getMessage());
        } catch (InvalidCharsetException e) {
            fail(e.getMessage());
        }
    }

}
