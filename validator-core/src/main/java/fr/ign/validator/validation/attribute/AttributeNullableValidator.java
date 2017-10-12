package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * Validation de l'aspect nullable sur un attribut
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
