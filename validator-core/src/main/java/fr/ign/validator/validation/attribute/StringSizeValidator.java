package fr.ign.validator.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation de la taille d'un attribut de type string
 * 
 * TODO valider aussi sur les autres types que String
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
				ErrorCode.ATTRIBUTE_SIZE_EXCEEDED,
				value.length(),
				attributeSize.toString()
			);
		}
	}

}
