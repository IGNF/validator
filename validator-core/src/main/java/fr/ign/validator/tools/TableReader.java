package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

/**
 * SHP/TAB file reader
 * 
 * Note :
 * <ul>
 * <li>Based on a csv conversion made by ogr2ogr</li>
 * <li>Puts csv file next to the original file</li>
 * </ul>
 * 
 * @author MBorne
 */
public class TableReader implements Iterator<String[]> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TableReader");

    /**
     * CSV file reader
     */
    private Iterator<CSVRecord> iterator;
    /**
     * File header
     */
    private String[] header;
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
    private TableReader(File csvFile, Charset preferedCharset) throws IOException {
        Charset charset = preferedCharset;
        if (!CharsetDetector.isValidCharset(csvFile, preferedCharset)) {
            charsetValid = false;
            charset = CharsetDetector.detectCharset(csvFile);
        }
        CSVParser parser = CSVParser.parse(csvFile, charset, CSVFormat.RFC4180);
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
            throw new IOException("Impossible de lire l'entête");
        }
        String[] fields = next();
        List<String> filteredFields = new ArrayList<String>();
        for (String field : fields) {
            if (field == null || field.isEmpty()) {
                continue;
            }
            filteredFields.add(field);
        }
        header = filteredFields.toArray(new String[filteredFields.size()]);
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

    @Override
    public String[] next() {
        CSVRecord row = iterator.next();
        return toArray(row);
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
        if (null == value || value.isEmpty()) {
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
     * Creates a reader from a file and a charset. If preferedCharset is invalid, a
     * valid charset is detected to read the file.
     * 
     * @param file
     * @param preferedCharset
     * @return
     * @throws IOException
     */
    public static TableReader createTableReader(File file, Charset preferedCharset) throws IOException {
        if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals("csv")) {
            return new TableReader(file, preferedCharset);
        }

        // Convert to CSV on first read
        File csvFile = CompanionFileUtils.getCompanionFile(file, "csv");
        if (!csvFile.exists()) {
            FileConverter converter = FileConverter.getInstance();
            converter.convertToCSV(file, csvFile, preferedCharset);
        }
        // ogr2ogr is supposed to always produced UTF-8 encoded CSV file
        return new TableReader(csvFile, StandardCharsets.UTF_8);
    }

}
