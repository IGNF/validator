package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;

/**
 * Validates that dateOfLastRevision is defined
 *
 * Note that CNIG DU and SUP profiles are more restrictive than INSPIRE on this
 * point and that date content is validated in core
 *
 * @author MBorne
 *
 */
public class CnigMetadataDateOfLastRevisionValidator extends AbstractCnigMetadataValidator {

    @Override
    public void validate(Context context, Metadata metadata) {
        Date value = metadata.getDateOfLastRevision();
        if (value == null) {
            context.report(
                CnigErrorCodes.CNIG_METADATA_DATEOFLASTREVISION_NOT_FOUND
            );
        }
    }

}
