package fr.ign.validator.cnig.validation.document;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentName;
import fr.ign.validator.cnig.model.DocumentType;
import fr.ign.validator.data.Document;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Checking the document contains at least one written material ("Pièce écrite")
 * 
 * @author MBorne
 *
 */
public class DocumentNameValidator implements Validator<Document>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocumentNameValidator");

    @Override
    public void validate(Context context, Document document) {
        log.info(MARKER, "Ensure that document name is correct...");
        DocumentName documentName = new DocumentName(document.getDocumentName());
        if (!documentName.isValid()) {
            log.error(
                MARKER, "skip document name validation as parsing fails ('{}')", document.getDocumentName()
            );
            return;
        }
        /*
         * "088" for department code is required since CNIG SUP 2016 ("88" is reported)
         */
        if (documentName.getDocumentType() == DocumentType.SUP && !context.getDocumentModelName().endsWith("_2013")) {
            String territory = documentName.getTerritory();
            if (StringUtils.length(territory) == 2 && !territory.equals("FR")) {
                context.report(
                    context.createError(CnigErrorCodes.CNIG_SUP_BAD_TERRITORY_CODE)
                        .setMessageParam("EMPRISE", territory)
                );
            }
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
