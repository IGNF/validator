package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.cnig.tools.CSV;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;

/**
 * Post-process PERIMETRE_SCOT table ensuring that it contains 1 row for SCOT
 */
public class PerimetreScotPostProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("PerimetreScotPostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        DocumentModel documentModel = context.getDocumentModel();
        String documentType = DocumentModelName.getDocumentType(documentModel.getName());
        if (!documentType.equalsIgnoreCase("SCOT")) {
            log.info(MARKER, "Skipped - document is not a SCOT");
            return;
        }

        File perimetreScotFile = new File(context.getDataDirectory(), "PERIMETRE_SCOT.csv");
        if (!perimetreScotFile.exists()) {
            log.warn(MARKER, "Skipped - PERIMETRE_SCOT not found");
            return;
        }
        int numRows = CSV.countRows(perimetreScotFile);
        log.info(MARKER, "Found {} row(s) in PERIMETRE_SCOT", numRows);
        if (numRows != 1) {
            log.info(MARKER, "Add CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE to report");
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE
                )
            );
        }
    }

}
