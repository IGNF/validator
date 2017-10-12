package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validate characters : 
 * <ul>
 * 	<li>check control characters</li>
 *  <li>check compatibility with LATIN (could become a context option)</li>
 * </ul>
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
			context.report(
				CoreErrorCodes.ATTRIBUTE_CHARACTERS_ILLEGAL,
				transform.transform(originalString),    // original string
				fixedString                             // transformed string
			);
		}
	}

}
