package fr.ign.validator.tools;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.ColumnNotFoundException;

/**
 * Read spatial and non spatial tables from different formats (CSV, Shapefile,
 * MapInfo, GML, GeoJSON,...).
 *
 * Note that :
 *
 * <ul>
 * <li>CSV files are read using apache-common-csv</li>
 * <li>Other formats are converted to CSV files (.vrows) using "ogr2ogr"</li>
 * <li>reader.charsetValid is set to false when charset is invalid</li>
 * <li>rows are aligned on the header (see {@link #next()})</li>
 * </ul>
 *
 * @author MBorne
 */
public class TableReader implements Iterator<String[]>, Closeable {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TableReader");

    /**
     * A validator specific extension for the CSV files produced by TableReader.
     */
    public static final String TMP_EXTENSION = "vrows";

    /**
     * CSV parser (closed with the TableReader)
     */
    private CSVParser parser;
    /**
     * CSV file reader
     */
    private Iterator<CSVRecord> iterator;
    /**
     * File header
     */
    private String[] header;
    /**
     * Number of fields in the header of the file (including empty names)
     */
    private int inputHeaderLength;
    /**
     * Position in the input rows of each column of the header
     */
    private int[] inputIndexes;
    /**
     * true if the last row has the same number of fields as the header
     */
    private boolean lastRowValid = true;
    /**
     * true if the charset is valid, false if detected
     */
    private boolean charsetValid = true;

    /**
     *
     * Reading file with given charset (validated by system).
     *
     *
     * @param file
     * @param charset
     * @throws IOException
     */
    TableReader(File csvFile, Charset preferedCharset) throws IOException {
        Charset charset = preferedCharset;
        if (!CharsetDetector.isValidCharset(csvFile, preferedCharset)) {
            charsetValid = false;
            charset = CharsetDetector.detectCharset(csvFile);
        }
        this.parser = CSVParser.parse(csvFile, charset, CSVFormat.RFC4180);
        this.iterator = parser.iterator();
        readHeader();
    }

    /**
     *
     * Reading file with given charset (validated by system).
     *
     *
     * @param file
     * @param charset
     * @throws IOException
     */
    TableReader(InputStream csvStream, Charset preferedCharset) throws IOException {
        this.parser = CSVParser.parse(csvStream, preferedCharset, CSVFormat.RFC4180);
        this.iterator = parser.iterator();
        readHeader();
    }

    /**
     * Indicate if charset used to open file is valid.
     *
     * @return
     */
    public boolean isCharsetValid() {
        return charsetValid;
    }

    /**
     * Header reading
     *
     *
     * Note : NULL or empty fields are filtered to avoid problems with files with
     * only one column
     *
     * @throws IOException
     */
    private void readHeader() throws IOException {
        if (!hasNext()) {
            close();
            throw new IOException("Fail to read header");
        }
        String[] fields = toArray(iterator.next());
        List<String> filteredFields = new ArrayList<String>();
        List<Integer> filteredIndexes = new ArrayList<Integer>();
        for (int i = 0; i < fields.length; i++) {
            String field = fields[i];
            if (field == null || field.isEmpty()) {
                continue;
            }
            filteredFields.add(field);
            filteredIndexes.add(i);
        }
        header = filteredFields.toArray(new String[filteredFields.size()]);
        inputHeaderLength = fields.length;
        inputIndexes = filteredIndexes.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * @return the header
     */
    public String[] getHeader() {
        return header;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /**
     * Read the next row aligned on the header : the values of columns with an empty
     * name are ignored and the missing values are null (see
     * {@link #isLastRowValid()} to detect such rows).
     *
     * @return values with the same size as the header
     */
    @Override
    public String[] next() {
        String[] row = toArray(iterator.next());
        lastRowValid = row.length == inputHeaderLength;
        if (lastRowValid && inputIndexes.length == row.length) {
            return row;
        }
        String[] result = new String[inputIndexes.length];
        for (int i = 0; i < inputIndexes.length; i++) {
            int inputIndex = inputIndexes[i];
            result[i] = inputIndex < row.length ? row[inputIndex] : null;
        }
        return result;
    }

    /**
     * Indicates if the last row returned by {@link #next()} has the same number of
     * fields as the header.
     *
     * @return
     */
    public boolean isLastRowValid() {
        return lastRowValid;
    }

    @Override
    public void close() throws IOException {
        parser.close();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param row
     * @return
     */
    private String[] toArray(CSVRecord row) {
        String[] result = new String[row.size()];
        for (int i = 0; i < row.size(); i++) {
            result[i] = nullifyEmptyString(trimString(row.get(i)));
        }
        return result;
    }

    /**
     * Trims a string
     *
     * @param value
     * @return
     */
    private String trimString(String value) {
        if (null == value) {
            return null;
        }
        return value.trim();
    }

    /**
     * Converts to NULL an empty string
     *
     * @param value
     */
    private String nullifyEmptyString(String value) {
        if (StringUtils.isEmpty(value)) {
            return null;
        } else {
            return value;
        }
    }

    /**
     * Finds the position of a column by its name in header
     *
     * @param string
     * @return
     */
    public int findColumn(String name) {
        return HeaderHelper.findColumn(header, name);
    }

    /**
     * Same as {@link #findColumn(String)} throwing exception when column is not
     * found.
     *
     * @param name
     * @return
     * @throws ColumnNotFoundException
     */
    public int findColumnRequired(String name) throws ColumnNotFoundException {
        int index = findColumn(name);
        if (index < 0) {
            throw new ColumnNotFoundException(name);
        }
        return index;
    }

    /**
     * Creates a reader from a file and a charset. If preferedCharset is invalid, a
     * valid charset is detected to read the file.
     *
     * @param file
     * @param preferedCharset
     * @return
     * @throws IOException
     */
    public static TableReader createTableReader(File file, Charset preferedCharset) throws IOException {
        log.debug(
            MARKER, "Create TableReader for '{}' (preferedCharset={})...",
            file.getAbsoluteFile(),
            preferedCharset
        );
        if (FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")) {
            return new TableReader(file, preferedCharset);
        }

        /*
         * Convert to CSV with a validator specific extension on first read
         */
        File tempFile = CompanionFileUtils.getCompanionFile(file, TMP_EXTENSION);
        if (!tempFile.exists()) {
            File csvFile = CompanionFileUtils.getCompanionFile(file, "csv");
            FileConverter converter = FileConverter.getInstance();
            converter.convertToCSV(file, csvFile, preferedCharset);
            if (!csvFile.exists()) {
                String message = String.format("Fail to convert %1s to CSV", file.getAbsolutePath());
                log.error(MARKER, message);
                throw new IOException(message);
            }
            log.info(MARKER, "{} -> {}", csvFile.getAbsolutePath(), tempFile.getAbsolutePath());
            csvFile.renameTo(tempFile);
        }
        // ogr2ogr is supposed to always produced UTF-8 encoded CSV file
        return new TableReader(tempFile, StandardCharsets.UTF_8);
    }

    /**
     *
     * @param url
     * @param charset
     * @return
     * @throws IOException
     */
    public static TableReader createTableReader(URL url) throws IOException {
        log.debug(
            MARKER, "Create TableReader for '{}' (charset={})...",
            url,
            StandardCharsets.UTF_8
        );
        return new TableReader(Networking.openStream(url), StandardCharsets.UTF_8);
    }

}
