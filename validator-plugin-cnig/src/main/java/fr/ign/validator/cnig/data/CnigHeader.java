package fr.ign.validator.cnig.data;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Header;
import fr.ign.validator.error.CoreErrorCodes;
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
    protected void reportMissingAttribute(Context context, AttributeType<?> missingAttribute) {
        if (missingAttribute.getConstraints().isPresenceRequired()
            // Mise en warning du deuxième cas dans un premier temps.
            && missingAttribute.getConstraints().isRequired()) {
            context.report(
                context.createError(CoreErrorCodes.TABLE_MISSING_ATTRIBUTE)
                    .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                    .setMessageParam("FILEPATH", relativePath)
            );
        } else if (missingAttribute.getConstraints().isPresenceRequired()) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_TABLE_MISSING_PRESENCE_OPTIONAL_ATTRIBUTE)
                    .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                    .setMessageParam("FILEPATH", relativePath)
            );
        } else {
            context.report(
                context.createError(CoreErrorCodes.TABLE_MISSING_PRESENCE_OPTIONAL_ATTRIBUTE)
                    .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                    .setMessageParam("FILEPATH", relativePath)
            );
        }
    }
}