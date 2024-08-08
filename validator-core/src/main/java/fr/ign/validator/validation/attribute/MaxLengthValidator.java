package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.AttributeConstraints;
import fr.ign.validator.validation.Validator;

/**
 *
 * Validates the size of a StringType Attribute
 *
 * @author MBorne
 *
 */
public class MaxLengthValidator<T> implements Validator<Attribute<T>> {

    @Override
    public void validate(Context context, Attribute<T> attribute) {
        T value = attribute.getBindedValue();

        if (value == null) {
            return;
        }

        AttributeConstraints constraints = attribute.getType().getConstraints();
        Integer maxLength = constraints.getMaxLength();
        if (maxLength == null || maxLength < 0) {
            return;
        }

        int length = value.toString().length();
        if (length > maxLength) {
            context.report(
                context.createError(CoreErrorCodes.ATTRIBUTE_SIZE_EXCEEDED)
                    .setMessageParam("VALUE_LENGTH", String.valueOf(length))
                    .setMessageParam("EXPECTED_LENGTH", maxLength.toString())
            );
        }
    }

}
