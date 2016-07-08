package fr.ign.validator.validation;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.Validator;

/**
 * 
 * Validation d'un StringType en fonction d'une expression régulière
 * 
 * @author MBorne
 *
 */
public class StringRegexpValidator implements Validator<Attribute<String>> {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		String value = attribute.getValue() ;
		
		if ( value == null ){
			return ;
		}
		
		if ( ! attribute.getType().hasRegexp() ){
			return ;
		}
		
		if (! value.matches(attribute.getType().getRegexp())) {
			context.report(
				ErrorCode.ATTRIBUTE_INVALID_REGEXP,
				value,
				attribute.getType().getRegexp()
			);
		}
		
	}

}
