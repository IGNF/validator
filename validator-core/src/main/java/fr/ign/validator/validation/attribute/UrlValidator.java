package fr.ign.validator.validation.attribute;

import java.net.URL;

import fr.ign.validator.Context;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.Validator;

/**
 * Validates the URL
 * @author MBorne
 *
 */
public class UrlValidator implements Validator<AttributeType<URL>>{

	@Override
	public void validate(Context context, AttributeType<URL> validatable) {
		// ATTRIBUTE_URL_NOT_FOUND / The given web address ([url]) references an unavailable resource
	}

	
	
}
