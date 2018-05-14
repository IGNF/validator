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
 * Ensures that "lineage" is defined and not empty
 * 
 * @author MBorne
 *
 */
public class LineageValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("LineageValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		String value = metadata.getLineage() ;
		log.info(MARKER, "metadata.lineage : {}", value);
		if ( StringUtils.isEmpty(value) ){
			context.report(
				CoreErrorCodes.METADATA_LINEAGE_NOT_FOUND
			);
		}
	}

}
