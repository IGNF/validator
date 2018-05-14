package fr.ign.validator.validation.file.metadata;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Extent;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "extents" is defined and is valid
 *  
 * @author MBorne
 *
 */
public class ExtentsValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("ExtentsValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		List<Extent> extents = metadata.getExtents() ;
		log.info(MARKER, "metadata.extents.size : {}", extents.size());
		if ( extents.isEmpty()  ){
			context.report(
				CoreErrorCodes.METADATA_EXTENTS_EMPTY
			);
		}
		int count = 1 ;
		for (Extent extent : extents) {
			if ( ! extent.getBoundingBox().isValid() ){
				context.report(
					CoreErrorCodes.METADATA_EXTENT_INVALID,
					extent.getBoundingBox().toString(),
					count,
					extents.size()
				);
			}
			count++;
		}
	}

	
	
}
