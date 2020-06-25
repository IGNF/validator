package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.validation.Validator;

public class VitesseMinValidator implements Validator<Attribute<Double>> {

    @Override
    public void validate(Context context, Attribute<Double> minAttribute) {

        if (minAttribute.getBindedValue() == null) {
            // nothing to validate
            return;
        }

        Row row = context.getDataByType(Row.class);
        FeatureType featureType = row.getMapping().getFeatureType();

        Attribute<Double> maxAttribute = null;
        // looking for VITESS_MAX attribute
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeType<?> type = featureType.getAttribute(i);
            if (!type.getName().equals("VITESS_MAX")) {
                continue;
            }
            if (!(type instanceof DoubleType)) {
                continue;
            }
            // value
            if (row.getMapping().getAttributeIndex(i) < 0) {
                return;
            }
            String inputValue = row.getValues()[row.getMapping().getAttributeIndex(i)];
            maxAttribute = ((DoubleType) type).newAttribute(inputValue);
        }

        // in some case we will not have any value, or a correct value
        // if max_value null, all is OK
        if (maxAttribute == null || maxAttribute.getBindedValue() == null) {
            return;
        }

        // minAtt && maxAtt are Double Attribute with no null value
        // if MAX > MIN, all is OK
        if (minAttribute.getBindedValue() < maxAttribute.getBindedValue()) {
            return;
        }
        reportMinError(context, minAttribute.getBindedValue().toString(), maxAttribute.getBindedValue().toString());

    }

    private void reportMinError(Context context, String minValue, String maxValue) {
        context.report(
            context.createError(DgprErrorCodes.DGPR_VITESSE_MIN_ERROR)
                .setMessageParam("VALUE_MIN", minValue)
                .setMessageParam("VALUE_MAX", maxValue)
        );
    }

}
