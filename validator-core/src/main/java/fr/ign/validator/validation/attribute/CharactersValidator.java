package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.string.StringFixer;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validate characters according to {@link StringFixer}
 * 
 * @author MBorne
 *
 * @param <T>
 */
public class CharactersValidator<T> implements Validator<Attribute<T>> {

	@Override
	public void validate(Context context, Attribute<T> validatable) {
		Object originalValue = validatable.getValue();
		if ( originalValue == null ){
			return;
		}
		
		String originalString = originalValue.toString();
		String fixedString    = context.getStringFixer().transform(originalString) ;

		if ( ! fixedString.equals(originalString) ){
			IsoControlEscaper transform = new IsoControlEscaper(false);
			/*
			 * if the string contains escaped controls after transform, 
			 * it's assumed that string contains illegal ("non displayable") characters 
			 */
			ErrorCode code = fixedString.contains("\\u") ? 
					CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL 
				  : CoreErrorCodes.ATTRIBUTE_CHARACTERS_REPLACED
			;
			context.report(
				code,
				transform.transform(originalString),    // original string
				fixedString                             // transformed string
			);
		}
	}

}
