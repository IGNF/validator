package fr.ign.validator.cnig.process;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.tools.TableReader;

/**
 * Post-process DOC_URBA_COM table ensuring that it contains at least 2 rows for
 * PLUi
 */
public class DocUrbaComPostProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocUrbaComPostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        DocumentModel documentModel = context.getDocumentModel();
        String documentType = DocumentModelName.getDocumentType(documentModel.getName());
        if (!documentType.equalsIgnoreCase("PLUi")) {
            log.info(MARKER, "Skipped - document is not a PLUi");
            return;
        }

        File docUrbaComFile = new File(context.getDataDirectory(), "DOC_URBA_COM.csv");
        if (!docUrbaComFile.exists()) {
            log.warn(MARKER, "Skipped - DOC_URBA_COM not found");
            return;
        }
        int numRows = countRows(docUrbaComFile);
        log.info(MARKER, "Found {} row(s) in DOC_URBA_COM", numRows);
        if (numRows < 2) {
            log.info(MARKER, "Add CNIG_DOC_URBA_COM_UNEXPECTED_SIZE to report");
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_DOC_URBA_COM_UNEXPECTED_SIZE
                )
            );
        }
    }

    /**
     * Count rows in a CSV file.
     * 
     * @param csvFile
     * @return
     * @throws IOException
     */
    private int countRows(File csvFile) throws IOException {
        TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        int numRows = 0;
        while (reader.hasNext()) {
            numRows++;
            reader.next();
        }
        return numRows;
    }

}
