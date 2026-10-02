package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.exception.ColumnNotFoundException;

/**
 *
 * Test CSV file reading with TableReader
 *
 * @author mickael
 *
 */
public class TableReaderCSVTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private TableReader createReader(String content) throws IOException {
        File file = folder.newFile("test.csv");
        FileUtils.writeStringToFile(file, content, StandardCharsets.UTF_8);
        return TableReader.createTableReader(file, StandardCharsets.UTF_8);
    }

    /**
     * Rows are aligned on the header (values of columns without name were shifted)
     */
    @Test
    public void testEmptyColumnName() throws IOException {
        try (TableReader reader = createReader("A,,B\r\n1,x,2\r\n")) {
            assertEquals(Arrays.asList("A", "B"), Arrays.asList(reader.getHeader()));
            assertEquals(Arrays.asList("1", "2"), Arrays.asList(reader.next()));
            assertTrue(reader.isLastRowValid());
            assertFalse(reader.hasNext());
        }
    }

    /**
     * Trailing comma (rows were rejected as TABLE_INVALID_ROW by TableNormalizer)
     */
    @Test
    public void testTrailingComma() throws IOException {
        try (TableReader reader = createReader("A,B,\r\n1,2,\r\n")) {
            assertEquals(Arrays.asList("A", "B"), Arrays.asList(reader.getHeader()));
            assertEquals(Arrays.asList("1", "2"), Arrays.asList(reader.next()));
            assertTrue(reader.isLastRowValid());
        }
    }

    /**
     * Missing values are null and the row is reported as invalid (was causing
     * ArrayIndexOutOfBoundsException while reading values)
     */
    @Test
    public void testShortAndLongRows() throws IOException {
        try (TableReader reader = createReader("A,B,C\r\n1\r\n1,2,3,4\r\n1,2,3\r\n")) {
            assertEquals(Arrays.asList("1", null, null), Arrays.asList(reader.next()));
            assertFalse(reader.isLastRowValid());
            assertEquals(Arrays.asList("1", "2", "3"), Arrays.asList(reader.next()));
            assertFalse(reader.isLastRowValid());
            assertEquals(Arrays.asList("1", "2", "3"), Arrays.asList(reader.next()));
            assertTrue(reader.isLastRowValid());
        }
    }

    @Test
    public void testReadEmpty() {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/empty.csv");
        assertThrows(IOException.class, () -> {
            TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        });
    }

    @Test
    public void testReadCsvUtf8() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertTrue(reader.isCharsetValid());
        checkExpectedSampleContent(reader);
    }

    @Test
    public void testFindColumn() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertTrue(reader.isCharsetValid());
        assertEquals(1, reader.findColumn("b"));
        assertEquals(1, reader.findColumn("B"));
        // not found
        assertEquals(-1, reader.findColumn("Z"));
    }

    @Test
    public void testFindColumnRequired() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertTrue(reader.isCharsetValid());
        assertEquals(1, reader.findColumnRequired("b"));
        assertEquals(1, reader.findColumnRequired("B"));
    }

    @Test
    public void testFindColumnRequiredNotFound() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(getClass(), "/csv/sample-utf8.csv");
        TableReader reader = TableReader.createTableReader(srcFile, StandardCharsets.UTF_8);
        assertTrue(reader.isCharsetValid());
        assertThrows(ColumnNotFoundException.class, () -> {
            reader.findColumnRequired("Z");
        });
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

    /**
     * Performs basic feature checks
     *
     * @param reader
     * @throws IOException
     */
    private void checkExpectedSampleContent(TableReader reader) throws IOException {
        String[] header = reader.getHeader();
        assertEquals(3, header.length);
        assertEquals("a", header[0]);
        assertEquals("b", header[1]);
        assertEquals("c", header[2]);

        String[] line1 = reader.next();
        assertEquals("a1", line1[0]);
        assertEquals("b1", line1[1]);
        assertEquals("c1", line1[2]);

        String[] line2 = reader.next();
        assertEquals("aé2", line2[0]);
        assertEquals("bé2", line2[1]);
        assertEquals("cé2", line2[2]);

        assertFalse(reader.hasNext());
    }

    private void checkExpectedSampleContentBadInterpretation(TableReader reader) throws IOException {
        String[] header = reader.getHeader();
        assertEquals(header.length, 3);
        assertEquals("a", header[0]);
        assertEquals("b", header[1]);
        assertEquals("c", header[2]);

        String[] line1 = reader.next();
        assertEquals("a1", line1[0]);
        assertEquals("b1", line1[1]);
        assertEquals("c1", line1[2]);

        String[] line2 = reader.next();
        assertEquals("aÃ©2", line2[0]);
        assertEquals("bÃ©2", line2[1]);
        assertEquals("cÃ©2", line2[2]);

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
