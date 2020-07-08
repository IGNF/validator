package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Test;

/**
 * 
 * Test CSV file reading with TableReader
 * 
 * @author mickael
 *
 */
public class TableReaderCSVTest {

    @Test
    public void testReadCsvUtf8() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertTrue(reader.isCharsetValid());
        checkExpectedSampleContent(reader);
    }

    @Test
    public void testReadCsvUtf8BadCharset() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.ISO_8859_1);
        // this problem can't be detected
        assertTrue(reader.isCharsetValid());
        checkExpectedSampleContentBadInterpretation(reader);
    }

    @Test
    public void testReadCsvLatin1() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-latin1.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.ISO_8859_1);
        assertTrue(reader.isCharsetValid());
        checkExpectedSampleContent(reader);
    }

    @Test
    public void testReadCsvLatin1BadCharset() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-latin1.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertFalse(reader.isCharsetValid());
        checkExpectedSampleContent(reader);
    }

    private void checkExpectedSampleContent(TableReader reader) throws IOException {
        String[] header = reader.getHeader();
        assertEquals(header.length, 3);
        assertEquals(header[0], "a");
        assertEquals(header[1], "b");
        assertEquals(header[2], "c");

        String[] line1 = reader.next();
        assertEquals(line1[0], "a1");
        assertEquals(line1[1], "b1");
        assertEquals(line1[2], "c1");

        String[] line2 = reader.next();
        assertEquals(line2[0], "aé2");
        assertEquals(line2[1], "bé2");
        assertEquals(line2[2], "cé2");

        assertFalse(reader.hasNext());
    }

    private void checkExpectedSampleContentBadInterpretation(TableReader reader) throws IOException {
        String[] header = reader.getHeader();
        assertEquals(header.length, 3);
        assertEquals(header[0], "a");
        assertEquals(header[1], "b");
        assertEquals(header[2], "c");

        String[] line1 = reader.next();
        assertEquals(line1[0], "a1");
        assertEquals(line1[1], "b1");
        assertEquals(line1[2], "c1");

        String[] line2 = reader.next();
        assertEquals(line2[0], "aÃ©2");
        assertEquals(line2[1], "bÃ©2");
        assertEquals(line2[2], "cÃ©2");

        assertFalse(reader.hasNext());
    }

    @Test
    public void testEmptyStringConvertedToNull() {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-with-empty.csv");
        try {
            TableReader csv = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
            String[] header = csv.getHeader();
            assertEquals(3, header.length);
            assertEquals("a", header[0]);
            assertEquals("b", header[1]);
            assertEquals("c", header[2]);

            String[] row = csv.next();
            assertEquals(3, row.length);
            assertEquals("ligne", row[0]);
            assertNull(row[1]);
            assertNull(row[2]);

            assertFalse(csv.hasNext());
        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTrim() {
        File file = ResourceHelper.getResourceFile(getClass(), "/csv/not-trimmed.csv");
        assertTrue(file.exists());
        try {
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

            String[] header = reader.getHeader();

            assertTrue(Arrays.asList(header).contains("A"));
            assertTrue(Arrays.asList(header).contains("B"));

            assertTrue(reader.hasNext());
            String[] row = reader.next();
            assertEquals("valeur A", row[0]);
            assertEquals("valeur B", row[1]);

        } catch (IOException e) {
            fail(e.getMessage());
        }
    }

}
