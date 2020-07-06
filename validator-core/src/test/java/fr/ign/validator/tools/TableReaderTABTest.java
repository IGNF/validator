package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class TableReaderTABTest {

    @Test
    public void testReadTabLatin1() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/tab_latin1/PRESCRIPTION_PCT.TAB");
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
    public void testReadTabUtf8() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/tab_utf8/PRESCRIPTION_PCT.tab");
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
    public void testReadZoneUrbaLatin1() {
        File file = ResourceHelper.getResourceFile(getClass(), "/data/tab_latin1/ZONE_URBA_41003.TAB");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);

            String[] header = reader.getHeader();
            assertEquals(10, header.length);

            int count = 0;
            while (reader.hasNext()) {
                String[] row = reader.next();
                assertEquals(header.length, row.length);
                count++;
            }
            assertEquals(36, count);
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
