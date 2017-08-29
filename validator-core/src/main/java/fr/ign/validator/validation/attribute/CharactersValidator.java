package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.tools.Characters;
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
		
		// check control characters...
		String escaped = Characters.escapeControls(originalValue.toString(),true);
		if ( ! escaped.equals(originalString) ){
			context.report(
				ErrorCode.ATTRIBUTE_CHARACTERS_CONTROL,
				Characters.escapeControls(originalValue.toString(),false) // highlight standard controls in reports
			);
		}

		// check latin1...
		String escapedLatin1 = Characters.escapeNonLatin1(originalValue.toString());
		if ( ! escapedLatin1.equals(originalString) ){
			context.report(
				ErrorCode.ATTRIBUTE_CHARACTERS_LATIN1,
				Characters.escapeControls(originalValue.toString(),false) // highlight standard controls in reports
			);
		}
	}

}
