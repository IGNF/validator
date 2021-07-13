package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * List of columns of a table associated to a FeatureType
 * 
 * @author MBorne
 *
 */
public class Header implements Validatable {

    /**
     * Relative path for the table in order to report errors.
     */
    private String relativePath;

    /**
     * mapping columns/FeatureType
     */
    private FeatureTypeMapper mapping;

    /**
     * @param columns
     * @param mapping
     */
    public Header(String relativePath, FeatureTypeMapper mapping) {
        this.relativePath = relativePath;
        this.mapping = mapping;
    }

    @Override
    public void validate(Context context) {
        context.beginData(this);

        FeatureType featureType = mapping.getFeatureType();

        /*
         * Attribute in data but not defined
         */
        for (String name : mapping.getUnexpectedAttributes()) {
            /*
             * Skipping "WKT" field (artificially created by conversion from dbf to csv)
             */
            if (name.equals("WKT")) {
                continue;
            }
            context.report(
                context.createError(CoreErrorCodes.TABLE_UNEXPECTED_ATTRIBUTE)
                    .setMessageParam("ATTRIBUTE_NAME", name)
            );
        }

        /*
         * Attribute missing in data
         */
        for (String name : mapping.getMissingAttributes()) {
            AttributeType<?> missingAttribute = featureType.getAttribute(name);
            context.beginModel(missingAttribute);

            if (missingAttribute.getName().equals("WKT")) {
                context.report(
                    context.createError(CoreErrorCodes.TABLE_MISSING_GEOMETRY)
                        .setMessageParam("FILEPATH", relativePath)
                );
            } else if (!missingAttribute.getConstraints().isRequired()) {
                context.report(
                    context.createError(CoreErrorCodes.TABLE_MISSING_NULLABLE_ATTRIBUTE)
                        .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                        .setMessageParam("FILEPATH", relativePath)
                );
            } else {
                context.report(
                    context.createError(CoreErrorCodes.TABLE_MISSING_ATTRIBUTE)
                        .setMessageParam("ATTRIBUTE_NAME", missingAttribute.getName())
                        .setMessageParam("FILEPATH", relativePath)
                );
            }
            context.endModel(missingAttribute);
        }

        context.endData(this);
    }

}
