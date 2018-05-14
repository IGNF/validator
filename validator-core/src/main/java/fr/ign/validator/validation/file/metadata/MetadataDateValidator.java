package fr.ign.validator.validation.file.metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "metadataDate" is defined and valid
 * 
 * @author MBorne
 *
 */
public class MetadataDateValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("MetadataDateValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		Date value = metadata.getMetadataDate() ;
		log.info(MARKER, "metadata.metadataDate : {}", value);
		if ( null == value ){
			context.report(
				CoreErrorCodes.METADATA_METADATADATE_NOT_FOUND
			);
		}else if ( ! value.isValid() ){
			context.report(
				CoreErrorCodes.METADATA_METADATADATE_INVALID,
				value
			);
		}
	}

}
