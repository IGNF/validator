package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates the size of a StringType Attribute
 * 
 * TODO validate other types than StringType
 * 
 * @author MBorne
 *
 */
public class StringSizeValidator implements Validator<Attribute<String>> {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		String value = attribute.getBindedValue() ;
		
		if ( value == null ){
			return ;
		}
		
		Integer attributeSize = attribute.getType().getSize() ;
		if ( attributeSize == null || attributeSize < 0 ){
			return ;
		}
		
		if ( value.length() > attributeSize ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_SIZE_EXCEEDED,
				value.length(),
				attributeSize.toString()
			);
		}
	}

}
