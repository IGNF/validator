package fr.ign.validator.validation;

import java.net.URL;

import fr.ign.validator.Context;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.Validator;

/**
 * Validation des URL
 * @author MBorne
 *
 */
public class UrlValidator implements Validator<AttributeType<URL>>{

	@Override
	public void validate(Context context, AttributeType<URL> validatable) {
		// TODO 
		// ATTRIBUTE_URL_NOT_FOUND / L'adresse web ([url]) renseignée pointe vers une ressource non disponible
	}

	
	
}
