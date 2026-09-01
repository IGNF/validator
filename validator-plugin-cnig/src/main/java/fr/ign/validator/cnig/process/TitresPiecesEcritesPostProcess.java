package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.cnig.tools.TitresPiecesEcritesOriginalPathBuilder;
import fr.ign.validator.data.Document;

/**
 * Post-process normalized {@code TITRES_PIECES_ECRITES.csv} to add
 * {@code ORIGINAL_PATH} column (relative path under {@code Pieces_ecrites}).
 */
public class TitresPiecesEcritesPostProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TitresPiecesEcritesPostProcess");

    private static final String[] DOCUMENT_TYPES = {
        "plu", "plui", "pos", "psmv", "cc", "scot"
    };

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
        if (!context.isNormalizeEnabled()) {
            log.info(MARKER, "Skipped - normalize is disabled");
            return;
        }
        if (!isDocumentWithPiecesEcrites(document)) {
            log.info(MARKER, "Skipped - document does not include Pieces_ecrites");
            return;
        }

        File tempDirectory = new File(context.getDataDirectory(), "tmp");
        TitresPiecesEcritesOriginalPathBuilder builder = new TitresPiecesEcritesOriginalPathBuilder(
            context.getCurrentDirectory(),
            tempDirectory
        );
        builder.addOriginalPathColumn(context.getDataDirectory());
    }

    private boolean isDocumentWithPiecesEcrites(Document document) {
        String documentType = DocumentModelName.getDocumentType(document.getDocumentModel().getName());
        if (documentType == null) {
            return false;
        }
        String regexp = ".*(" + String.join("|", DOCUMENT_TYPES) + ").*";
        return documentType.toLowerCase().matches(regexp);
    }

}
