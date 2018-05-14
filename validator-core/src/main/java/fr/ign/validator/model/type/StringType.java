package fr.ign.validator.model.type;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.ListOfValuesValidator;
import fr.ign.validator.validation.attribute.StringRegexpValidator;
import fr.ign.validator.validation.attribute.StringSizeValidator;

/**
 * Represents a character string
 * 
 * @author MBorne
 *
 */
public class StringType extends AttributeType<String> {
	
	public StringType() {
		super(String.class);
		addValidator(new StringSizeValidator());
		addValidator(new StringRegexpValidator());
		addValidator(new ListOfValuesValidator());
	}

	@Override
	public String getTypeName() {
		return "String" ;
	}
	
	@Override
	public String bind(Object value) {
		if ( value == null || value instanceof String ){
			return (String)value ;
		}
		return value.toString() ;
	}

	@Override
	public String format(String value) {
		return value ;
	}

}
