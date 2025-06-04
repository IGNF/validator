package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.tools.CnigSpecificationFinder;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

/**
 * Ensures that "specifications" contains an element with the following form :
 *
 * ex : "CNIG PLU v2013"
 *
 *
 * @author MBorne
 *
 */
public class CnigSpecificationsValidator extends AbstractCnigMetadataValidator {

    @Override
    public void validate(Context context, Metadata metadata) {
        Specification specification = CnigSpecificationFinder.findCnigSpecification(metadata);
        if (specification == null) {
            context.report(
                CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND
            );
            return;
        }
    }

}
