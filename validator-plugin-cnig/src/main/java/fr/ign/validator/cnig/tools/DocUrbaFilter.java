package fr.ign.validator.cnig.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

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
 *
 * Filter table DOC_URBA to keep rows with the expected IDURBA and outputs
 * the number of rows, idurba and typeref (cadastral reference) if available.
 *
 * @author MBorne
 *
 */
public class DocUrbaFilter {
    private static final String DEFAULT_TYPEREF = "01";
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocUrbaFilter");

    public class Result {
        public int count = 0;
        public String idurba;
        public String typeref = DEFAULT_TYPEREF;
    }

    /**
     * IDURBA naming convention
     */
    private IdurbaFormat idurbaFormat;
    /**
     * Folder name for the document
     */
    private String documentName;

    /**
     * @param idurbaFormat
     */
    public DocUrbaFilter(IdurbaFormat idurbaFormat, String documentName) {
        this.idurbaFormat = idurbaFormat;
        this.documentName = documentName;
    }

    /**
     * Finds a typeref in docUrbaFile according to documentName
     *
     * @param documentName
     * @return
     */
    public Result process(File docUrbaFile) {
        Result result = new Result();

        if (!docUrbaFile.exists()) {
            log.error(MARKER, "DOC_URBA not found");
            return result;
        }

        try {
            TableReader reader = TableReader.createTableReader(docUrbaFile, StandardCharsets.UTF_8);
            /*
             * Find required columns in DOC_URBA.csv
             */
            int indexIdurba = reader.findColumn("IDURBA");
            if (indexIdurba < 0) {
                log.error(MARKER, "IDURBA not found in DOC_URBA");
                return result;
            }

            int indexTyperef = reader.findColumn("TYPEREF");
            if (indexTyperef < 0) {
                log.warn(MARKER, "TYPEREF not found in DOC_URBA");
            }

            /*
             * Create writer for filtered file
             */
            File newIdUrbaFile = new File(docUrbaFile.getParentFile(), "DOC_URBA.new.csv");
            BufferedWriter fileWriter = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(newIdUrbaFile), StandardCharsets.UTF_8)
            );
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.RFC4180);

            /*
             * Write header
             */
            printer.printRecord(reader.getHeader());

            /*
             * Search of row corresponding to documentName
             */

            while (reader.hasNext()) {
                String[] row = reader.next();
                String idurba = row[indexIdurba];

                if (StringUtils.isEmpty(idurba)) {
                    continue;
                }

                if (!idurbaFormat.isValid(idurba, documentName)) {
                    continue;
                }

                result.count++;
                // retrieve IDURBA
                log.info(MARKER, "Found IDURBA={}", idurba);
                result.idurba = idurba;
                // retreive TYPEREF if available
                if (indexTyperef >= 0) {
                    String typeref = row[indexTyperef];
                    log.info(MARKER, "Found TYPEREF={} for IDURBA={}", typeref, idurba);
                    if (!StringUtils.isEmpty(typeref)) {
                        result.typeref = typeref;
                    }
                }

                printer.printRecord(row);
            }

            /*
             * close writer and replace original file
             */
            printer.close();
            docUrbaFile.delete();
            FileUtils.moveFile(newIdUrbaFile, docUrbaFile);
        } catch (Exception e) {
            log.error(MARKER, "Erreur dans la lecture de DOC_URBA.csv");
        }

        return result;
    }

}
