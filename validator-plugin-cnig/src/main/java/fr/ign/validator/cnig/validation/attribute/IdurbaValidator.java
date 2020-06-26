package fr.ign.validator.cnig.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.tools.IdurbaFormat;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.validation.Validator;

public class IdurbaValidator implements Validator<Attribute<String>> {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("IdurbaFormatValidator");

    /**
     * Format corresponding to the version of the DocumentModel
     */
    private IdurbaFormat idurbaFormat;
    /**
     * The name of the document
     */
    private String documentName;

    public IdurbaValidator(IdurbaFormat idurbaFormat, String documentName) {
        this.idurbaFormat = idurbaFormat;
        this.documentName = documentName;
    }

    /**
     * Test if string value is valid
     * 
     * @param value
     * @return
     */
    public boolean isValid(String value) {
        return idurbaFormat.isValid(value, documentName);
    }

    @Override
    public void validate(Context context, Attribute<String> attribute) {
        String value = attribute.getBindedValue();
        if (isValid(value)) {
            return;
        }

        /* valid format not found */
        context.report(
            context.createError(CnigErrorCodes.CNIG_IDURBA_UNEXPECTED)
                .setMessageParam("VALUE", attribute.getBindedValue())
                .setMessageParam("EXPECTED_VALUE", idurbaFormat.getRegexpHelp(documentName))
        );
    }
}
