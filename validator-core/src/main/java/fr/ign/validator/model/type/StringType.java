package fr.ign.validator.model.type;

import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.ListOfValuesValidator;
import fr.ign.validator.validation.StringRegexpValidator;
import fr.ign.validator.validation.StringSizeValidator;

/**
 * Représente un champ de type chaîne de caractère
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

	@Override
	public Attribute<String> newAttribute(String object) {
		return new Attribute<String>(this,object);
	}
}
