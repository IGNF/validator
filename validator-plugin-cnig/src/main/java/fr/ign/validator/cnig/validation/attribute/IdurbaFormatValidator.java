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

/**
 * 
 * Dedicated to IDURBA validation in DOC_URBA table.
 * 
 * Ensure that the value matches one of the supported {@link IdurbaFormat}.
 * 
 * @author MBorne
 *
 */
public class IdurbaFormatValidator implements Validator<Attribute<String>> {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("IDURBA_VALIDATOR");

    /**
     * idurbaHelper configured according to document model (see beforeMatching)
     */
    private IdurbaFormat idurbaHelper;

    public IdurbaFormatValidator(IdurbaFormat idurbaHelper) {
        this.idurbaHelper = idurbaHelper;
    }

    @Override
    public void validate(Context context, Attribute<String> attribute) {
        String value = attribute.getBindedValue();
        if (!idurbaHelper.isValid(value)) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_IDURBA_INVALID)
                    .setMessageParam("VALUE", attribute.getBindedValue())
                    .setMessageParam("IDURBA_FORMAT", idurbaHelper.getRegexpHelp())
            );
        }
    }

}
