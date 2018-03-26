package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates a StringType Attribute according to a regexp
 * 
 * @author MBorne
 *
 */
public class StringRegexpValidator implements Validator<Attribute<String>> {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		String value = attribute.getBindedValue() ;
		
		if ( value == null ){
			return ;
		}
		
		if ( ! attribute.getType().hasRegexp() ){
			return ;
		}
		
		if (! value.matches(attribute.getType().getRegexp())) {
			context.report(
				CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP,
				value,
				attribute.getType().getRegexp()
			);
		}
		
	}

}
