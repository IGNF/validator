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

import fr.ign.validator.exception.InvalidCharsetException;

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
     * 
     * Reading file with given charset (validated by system).
     * 
     * 
     * @param file
     * @param charset
     * @throws IOException
     */
    private TableReader(File csvFile, Charset charset) throws IOException {
        CSVParser parser = CSVParser.parse(csvFile, charset, CSVFormat.RFC4180);
        this.iterator = parser.iterator();
        readHeader();
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
     * Creates a reader from a file and a charset (throws exception if charset is
     * invalid)
     * 
     * @param file
     * @param charset
     * @return
     * @throws IOException
     * @throws InvalidCharsetException
     */
    public static TableReader createTableReader(File file, Charset charset) throws IOException,
        InvalidCharsetException {
        if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals("csv")) {
            if (!CharsetDetector.isValidCharset(file, charset)) {
                throw new InvalidCharsetException(
                    String.format(
                        "Le fichier %s n'est pas valide pour la charset %s",
                        file.toString(),
                        charset.toString()
                    )
                );
            }
            return new TableReader(file, charset);
        }

        /* convert to CSV */
        File csvFile = CompanionFileUtils.getCompanionFile(file, "csv");
        FileConverter converter = FileConverter.getInstance();
        converter.convertToCSV(file, csvFile, charset);
        if (!CharsetDetector.isValidCharset(csvFile, StandardCharsets.UTF_8)) {
            throw new InvalidCharsetException(
                String.format(
                    "Le fichier %s n'est pas valide pour la charset %s",
                    file.toString(),
                    charset.toString()
                )
            );
        }
        return new TableReader(csvFile, StandardCharsets.UTF_8);
    }

    /**
     * Create TableReader with a given charset. If the given charset is invalid,
     * redirect to createTableReaderDetectCharset
     * 
     * @param file
     * @param charset
     * @return
     * @throws IOException
     */
    public static TableReader createTableReaderPreferedCharset(File file, Charset charset) throws IOException {
        try {
            return TableReader.createTableReader(file, charset);
        } catch (InvalidCharsetException e) {
            log.info(
                MARKER, "Charset invalide, tentative d'autodétection de la charset pour la validation de {}", file
            );
            return TableReader.createTableReaderDetectCharset(file);
        }
    }

    /**
     * Create TableReader with charset autodetection
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static TableReader createTableReaderDetectCharset(File file) throws IOException {
        if (FilenameUtils.getExtension(file.getName()).toLowerCase().equals("csv")) {
            Charset charset = CharsetDetector.detectCharset(file);
            return new TableReader(file, charset);
        }

        File csvFile = CompanionFileUtils.getCompanionFile(file, "csv");
        FileConverter converter = FileConverter.getInstance();
        converter.convertToCSV(file, csvFile, StandardCharsets.UTF_8);
        Charset charset = CharsetDetector.detectCharset(csvFile);
        return new TableReader(csvFile, charset);
    }

}
