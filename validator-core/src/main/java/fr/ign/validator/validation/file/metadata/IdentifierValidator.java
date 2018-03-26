package fr.ign.validator.validation.file.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "identifier" is defined and not empty
 * 
 * @author MBorne
 *
 */
public class IdentifierValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("IdentifierValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		String value = metadata.getIdentifier() ;
		log.info(MARKER, "metadata.identifier : {}", value);
		if ( StringUtils.isEmpty(value) ){
			context.report(
				CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND
			);
		}
	}

}
