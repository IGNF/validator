package fr.ign.validator.cnig.validation.document;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.validation.Validator;

public class PieceEcriteOnlyPdfValidator implements Validator<Document>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("PieceEcriteOnlyPdfValidator");

    private static final String PIECE_ECRITE_DIRNAME = "Pieces_ecrites";
    private static final String PIECE_ECRITE_FILENAME = "TITRES_PIECES_ECRITES";
    private static final String[] DOCUMENT_TYPES = {
        "plu", "plui", "pos", "psmv", "cc", "scot"
    };

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        document.getDocumentModel().addValidator(this);
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
    }

    @Override
    public void validate(Context context, Document document) {
        String documentType = DocumentModelName.getDocumentType(document.getDocumentModel().getName());
        String regepx = ".*(" + String.join("|", DOCUMENT_TYPES) + ").*";
        if (!documentType.toLowerCase().matches(regepx)) {
            log.info(
                MARKER,
                "Skipped - document is not a PLU, PLUi, a POS, a SCoT, a PSMV, a CC, therefore does not include piece ecrite directory"
            );
            return;
        }

        DocumentModel documentModel = document.getDocumentModel();
        DocumentFile pieceEcriteFile = null;
        List<DocumentFile> documentFiles = document.getDocumentFilesByModel(
            documentModel.getFileModelByName(PIECE_ECRITE_FILENAME)
        );
        if (documentFiles.size() > 0) {
            pieceEcriteFile = documentFiles.get(0);
        }

        File documentDirectory = context.getCurrentDirectory();
        if (!documentDirectory.exists()) {
            log.info(MARKER, "Le repertoire document n'est pas valide");
            return;
        }
        log.info(MARKER, "Search non pdf files in directory : pieces ecrites...");

        File pieceEcriteDirectory = new File(documentDirectory, PIECE_ECRITE_DIRNAME);
        if (!pieceEcriteDirectory.exists()) {
            log.info(MARKER, "Le repertoire piece ecrite n'existe pas");
            return;
        }

        String[] extensions = null;
        Collection<File> files = FileUtils.listFiles(pieceEcriteDirectory, extensions, true);
        for (File file : files) {
            // get extension
            String extension = FilenameUtils.getExtension(file.getName());
            if (extension.equals("pdf")) {
                continue;
            }
            if (pieceEcriteFile != null && pieceEcriteFile.getPath().toString().equals(file.getPath())) {
                continue;
            }
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_PIECE_ECRITE_ONLY_PDF
                ).setFile(file.getName())
            );
        }

    }

}
