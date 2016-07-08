package fr.ign.validator.validation;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.Validator;

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
		if ( attribute.getValue() == null ){
			context.report(ErrorCode.ATTRIBUTE_UNEXPECTED_NULL);
		}
	}

}
