package fr.ign.validator.cnig.data;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Header;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;

public class CnigHeader extends Header {

    /**
     * @param columns
     * @param mapping
     */
    public CnigHeader(String relativePath, FeatureTypeMapper mapping) {
        super(relativePath, mapping);
    }

    @Override
    public void reportTableMissingPresenceOptionalAttribute(AttributeType<?> missingAttribute, Context context) {
        if (!missingAttribute.getConstraints().isPresenceRequiredModified()) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_TABLE_MISSING_PRESENCE_OPTIONAL_ATTRIBUTE)
                    .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                    .setMessageParam("FILEPATH", this.getRelativePath())
            );
        }
    }
}