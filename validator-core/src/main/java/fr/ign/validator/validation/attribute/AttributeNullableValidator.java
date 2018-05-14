package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * Validates if an attribute is nullable or not
 * (reports unexpected null attribute)
 * 
 * @author MBorne
 *
 */
public class AttributeNullableValidator<T> implements Validator<Attribute<T>> {

	@Override
	public void validate(Context context, Attribute<T> attribute) {
		if ( attribute.getType().isNullable() ){
			return ;
		}
		if ( attribute.getBindedValue() == null ){
			context.report(CoreErrorCodes.ATTRIBUTE_UNEXPECTED_NULL);
		}
	}

}
