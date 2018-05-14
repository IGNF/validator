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
 * Ensures that "abstract" is defined and not empty
 *  
 * @author MBorne
 *
 */
public class AbstractValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("AbstractValidator") ;	

	@Override
	public void validate(Context context, Metadata metadata) {
		String value = metadata.getAbstract() ;
		log.info(MARKER, "abstract : {}", value);
		if ( StringUtils.isEmpty(value) ){
			context.report(
				CoreErrorCodes.METADATA_ABSTRACT_NOT_FOUND
			);
		}
	}

}
