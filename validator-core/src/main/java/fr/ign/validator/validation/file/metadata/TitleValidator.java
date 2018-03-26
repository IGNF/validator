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
 * Ensures that "title" is defined and not empty
 *  
 * @author MBorne
 *
 */
public class TitleValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("TitleValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		String value = metadata.getTitle() ;
		log.info(MARKER, "metadata.title : {}", value);
		if ( StringUtils.isEmpty(value)  ){
			context.report(
				CoreErrorCodes.METADATA_TITLE_NOT_FOUND
			);
		}
	}

}
