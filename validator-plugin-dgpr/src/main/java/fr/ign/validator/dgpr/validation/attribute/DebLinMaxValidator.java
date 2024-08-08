package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.validation.Validator;

public class DebLinMaxValidator implements Validator<Attribute<Double>> {

    @Override
    public void validate(Context context, Attribute<Double> debMaxAttribute) {

        if (debMaxAttribute.getBindedValue() == null) {
            // nothing to validate
            return;
        }

        Row row = context.getDataByType(Row.class);
        FeatureType featureType = row.getMapping().getFeatureType();

        Attribute<Double> debMinAttribute = null;
        // looking for DEBLIN_MIN attribute
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeType<?> type = featureType.getAttribute(i);
            if (!type.getName().equals("DEBLIN_MIN")) {
                continue;
            }
            if (!(type instanceof DoubleType)) {
                continue;
            }
            // value
            if (row.getMapping().getAttributeIndex(i) < 0) {
                reportNoMinValue(context, debMaxAttribute.getBindedValue().toString());
                return;
            }
            String inputValue = row.getValues()[row.getMapping().getAttributeIndex(i)];
            debMinAttribute = ((DoubleType) type).newAttribute(inputValue);
        }

        // in some case we will not have any value, or a correct value
        if (debMinAttribute == null || debMinAttribute.getBindedValue() == null) {
            reportNoMinValue(context, debMaxAttribute.getBindedValue().toString());
            return;
        }

        // debMax && debMin are Double Attribute with no null value
        if (debMaxAttribute.getBindedValue() >= debMinAttribute.getBindedValue()) {
            return;
        }
        reportMaxError(
            context, debMaxAttribute.getBindedValue().toString(), debMinAttribute.getBindedValue().toString()
        );

    }

    /**
     *
     * @param context
     * @param debMaxValue
     */
    private void reportNoMinValue(Context context, String debMaxValue) {
        context.report(
            context.createError(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR)
                .setMessageParam("VALUE_MAX", debMaxValue)
                .setMessageParam("VALUE_MIN", "non renseignée")
        );
    }

    /**
     * Report if DebMin > DebMax
     *
     * @param context
     * @param debMaxValue
     */
    private void reportMaxError(Context context, String debMaxValue, String debMinValue) {
        context.report(
            context.createError(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR)
                .setMessageParam("VALUE_MAX", debMaxValue)
                .setMessageParam("VALUE_MIN", debMinValue)
        );
    }

}
