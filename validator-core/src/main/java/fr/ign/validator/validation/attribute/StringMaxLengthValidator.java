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
public class StringMaxLengthValidator implements Validator<Attribute<String>> {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		String value = attribute.getBindedValue() ;
		
		if ( value == null ){
			return ;
		}
		
		AttributeConstraints constraints = attribute.getType().getConstraints();
		Integer maxLength = constraints.getMaxLength() ;
		if ( maxLength == null || maxLength < 0 ){
			return ;
		}
		
		if ( value.length() > maxLength ){
			context.report(context.createError(CoreErrorCodes.ATTRIBUTE_SIZE_EXCEEDED)
				.setMessageParam("VALUE_LENGTH", String.valueOf(value.length()))
				.setMessageParam("EXPECTED_LENGTH", maxLength.toString())
			);
		}
	}

}
