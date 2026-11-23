package fr.ign.validator.validation.attribute;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.Validator;

/**
 *
 * Validates according to a list of values
 *
 * @author MBorne
 *
 */
public class StringEnumValuesValidator implements Validator<Attribute<String>> {

    @Override
    public void validate(Context context, Attribute<String> attribute) {
        String value = attribute.getBindedValue();

        if (value == null) {
            return;
        }

        AttributeType<String> attributeType = attribute.getType();
        if (!attributeType.getConstraints().hasEnumValues()) {
            return;
        }

        List<String> enumValues = attributeType.getConstraints().getEnumValues();
        String separator = attributeType.getConstraints().getEnumSeparator();
        if (StringUtils.isEmpty(separator)) {
            if (!enumValues.contains(value)) {
                reportUnexpectedValue(context, value, enumValues);
            }
            return;
        }

        /*
         * several values separated by the separator, each one must be allowed
         */
        for (String part : StringUtils.splitByWholeSeparatorPreserveAllTokens(value, separator)) {
            String trimmed = part.trim();
            if (!enumValues.contains(trimmed)) {
                reportUnexpectedValue(context, trimmed, enumValues);
            }
        }
    }

    private void reportUnexpectedValue(Context context, String value, List<String> enumValues) {
        context.report(
            context.createError(CoreErrorCodes.ATTRIBUTE_UNEXPECTED_VALUE)
                .setMessageParam("VALUE", value)
                .setMessageParam("EXPECTED_VALUES", formatListOfValues(enumValues))
        );
    }

    /**
     * Formatting a list of values for error report
     *
     * @param listOfValues
     * @return
     */
    private String formatListOfValues(List<String> listOfValues) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String string : listOfValues) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(string);
        }
        return sb.toString();
    }

}
