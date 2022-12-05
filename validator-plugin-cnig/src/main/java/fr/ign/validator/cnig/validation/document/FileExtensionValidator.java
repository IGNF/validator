package fr.ign.validator.cnig.validation.document;

import java.io.File;
import java.util.Collection;

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
import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.validation.Validator;

public class FileExtensionValidator implements Validator<Document>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("FileExtensionValidator");

    private static final String[] VALID_FILE_EXTENSION = {
        "xml", "pdf", "csv", "dbf", "shp", "geojson", "gml"
    };
    private static final String ROOT_SHAPE_EXTENSION = "shp";
    private static final String ROOT_MAPINFO_EXTENSION = "dbf";
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

        File documentDirectory = context.getCurrentDirectory();
        if (!documentDirectory.exists()) {
            log.info(MARKER, "Le repertoire document n'est pas valide");
            return;
        }

        String[] extensions = null;
        Collection<File> files = FileUtils.listFiles(documentDirectory, extensions, true);
        for (File file : files) {
            // get extension
            String extension = FilenameUtils.getExtension(file.getName());
            if (extension.toLowerCase().matches("(" + String.join("|", VALID_FILE_EXTENSION) + ")")) {
                continue;
            }
            // test if there is a dbf, or shp compagnion file
            if (CompanionFileUtils.hasCompanionFile(file, ROOT_SHAPE_EXTENSION)) {
                continue;
            }
            if (CompanionFileUtils.hasCompanionFile(file, ROOT_MAPINFO_EXTENSION)) {
                continue;
            }
            context.report(
                context.createError(
                    CnigErrorCodes.CNIG_FILE_EXTENSION_INVALID
                ).setFile(file.getName())
            );
        }

    }

}
