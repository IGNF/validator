package fr.ign.validator.validation.attribute;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.AttributeConstraints;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates a StringType Attribute according to a regexp
 * 
 * @author MBorne
 *
 */
public class StringPatternValidator implements Validator<Attribute<String>> {

    @Override
    public void validate(Context context, Attribute<String> attribute) {
        String value = attribute.getBindedValue();

        if (value == null) {
            return;
        }

        AttributeConstraints constraints = attribute.getType().getConstraints();
        if (StringUtils.isEmpty(constraints.getPattern())) {
            return;
        }

        if (!value.matches(constraints.getPattern())) {
            context.report(
                context.createError(CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP)
                    .setMessageParam("VALUE", value)
                    .setMessageParam("REGEXP", constraints.getPattern())
            );
        }

    }

}
