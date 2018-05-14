package fr.ign.validator.validation.file.metadata;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Resolution;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "spatialResolutions" is not empty and that content (denominator or distance) is valid. 
 *  
 * @author MBorne
 *
 */
public class SpatialResolutionsValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("SpatialResolutionsValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		List<Resolution> resolutions = metadata.getSpatialResolutions() ;
		log.info(MARKER, "metadata.resolutions.size : {}", resolutions.size());
		if ( resolutions.isEmpty()  ){
			context.report(
				CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY
			);
		}
		int count = 1 ;
		for (Resolution resolution : resolutions) {
			String denominator = resolution.getDenominator();
			if ( ! isValidInteger(denominator) ){
				context.report(
					CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR,
					denominator,
					count,
					resolutions.size()
				);
			}
			
			String distance = resolution.getDistance();
			if ( ! isValidDouble(distance) ){
				context.report(
					CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DISTANCE,
					distance,
					count,
					resolutions.size()
				);
			}
			count++;
		}
	}

	/**
	 * TODO move to IntegerType
	 * @param value
	 * @return
	 */
	private boolean isValidInteger(String value){
		if ( value == null ){
			return true;
		}
		try {
			Integer.valueOf(value);
		}catch(Exception e){
			return false;
		}
		return true;		
	}
	
	/**
	 * TODO move to DoubleType
	 * @param value
	 * @return
	 */
	private boolean isValidDouble(String value){
		if ( value == null ){
			return true;
		}
		try {
			Double.valueOf(value);
		}catch(Exception e){
			return false;
		}
		return true;		
	}
}
