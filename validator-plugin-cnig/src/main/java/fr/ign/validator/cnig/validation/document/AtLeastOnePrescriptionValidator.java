package fr.ign.validator.cnig.validation.document;

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
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.validation.Validator;

public class AtLeastOnePrescriptionValidator implements Validator<Document>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("AtLeastOnePrescriptionValidator");

    @Override
    public void validate(Context context, Document document) {
        boolean hasPrescription = false;
        for (FileModel fileModel : document.getDocumentModel().getFileModels()) {
            if (fileModel.getName().contains("PRESCRIPTION")) {
                hasPrescription = true;
            }
        }
        if (!hasPrescription) {
            log.info(MARKER, "Skip control if document model has no PRESCRIPTION table.");
            return;
        }
        log.info(MARKER, "Ensure that document contains at least one PRESCRIPTION file...");
        int count = 0;
        for (DocumentFile documentFile : document.getDocumentFiles()) {
            if (!(documentFile.getFileModel() instanceof TableModel)) {
                continue;
            }
            if (documentFile.getFileModel().getName().contains("PRESCRIPTION")) {
                count++;
            }
        }
        log.info(MARKER, "Found {} PRESCRIPTION file(s).", count);
        if (count == 0) {
            context.report(CnigErrorCodes.CNIG_PRESCRIPTION_NOT_FOUND);
        }
    }

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        document.getDocumentModel().addValidator(this);
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {

    }

}
