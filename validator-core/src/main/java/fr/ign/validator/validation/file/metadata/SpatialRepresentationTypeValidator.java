package fr.ign.validator.validation.file.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.SpatialRepresentationTypeCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates metadata.spatialRepresentationType
 * 
 * @author MBorne
 *
 */
public class SpatialRepresentationTypeValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("SpatialRepresentationTypeValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		SpatialRepresentationTypeCode code = metadata.getSpatialRepresentationType() ;
		log.info(MARKER, "metadata.type : {}", code);
		if ( null == code ){
			context.report(
				CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND
			);
		}else if ( ! code.isAllowedValue() ){
			context.report(
				CoreErrorCodes.METADATA_SPATIALREPRESENTATIONTYPE_INVALID,
				code.getValue(),
				StringUtils.join(code.getCodeList().getAllowedValues(), ", ")
			);
		}
	}

}
