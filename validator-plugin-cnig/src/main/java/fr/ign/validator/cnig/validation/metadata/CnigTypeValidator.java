package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.ScopeCode;

/**
 * Ensures that "type" is defined to "dataset"
 *
 * @author MBorne
 *
 */
public class CnigTypeValidator extends AbstractCnigMetadataValidator {

    @Override
    public void validate(Context context, Metadata metadata) {
        ScopeCode code = metadata.getType();
        if (code == null) {
            // already reported
            return;
        }
        if (!code.equals(ScopeCode.valueOf("dataset")) && !code.equals(ScopeCode.valueOf("serie"))) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_METADATA_TYPE_INVALID)
                    .setMessageParam("VALUE", code.getValue())
            );
        }
    }

}
