package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

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
