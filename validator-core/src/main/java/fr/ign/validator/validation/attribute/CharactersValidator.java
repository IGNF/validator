package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.tools.Characters;
import fr.ign.validator.tools.CharacterValidationOptions;
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
		
		CharacterValidationOptions options = context.getCharacterValidationOptions();
		String normalized = Characters.normalize(originalString, options) ;

		if ( ! normalized.equals(originalString) ){
			context.report(
				ErrorCode.ATTRIBUTE_CHARACTERS_ILLEGAL,
				Characters.escape(originalString.toString(),options), // original string
				normalized                                            // transformed string
			);
		}
	}

}
