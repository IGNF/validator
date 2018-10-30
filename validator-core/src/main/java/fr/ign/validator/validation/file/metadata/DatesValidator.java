package fr.ign.validator.validation.file.metadata;

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
 * Validates dateOfPublication, dateOfLastRevision and dateOfCreation :
 * 
 * <ul>
 * 	<li>Ensures that at least one date is defined</li>
 *  <li>Ensures that each defined date is in a valid format</li>
 * </ul>
 *  
 * @author MBorne
 *
 */
public class DatesValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DatesValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		if ( metadata.getDateOfPublication() == null 
		  && metadata.getDateOfLastRevision() == null
		  && metadata.getDateOfCreation() == null
		){
			context.report(context.createError(CoreErrorCodes.METADATA_DATES_NOT_FOUND));
			return ;
		}

		if ( metadata.getDateOfPublication() != null && ! metadata.getDateOfPublication().isValid() ){
			log.info(MARKER, "metadata.dateOfPublication : {}", metadata.getDateOfPublication());
			context.report(context.createError(CoreErrorCodes.METADATA_DATEOFPUBLICATION_INVALID)
				.setMessageParam("VALUE", metadata.getDateOfPublication().toString())
			);
		}
		if ( metadata.getDateOfLastRevision() != null && ! metadata.getDateOfLastRevision().isValid() ){
			log.info(MARKER, "metadata.dateOfLastRevision : {}", metadata.getDateOfLastRevision());
			context.report(context.createError(CoreErrorCodes.METADATA_DATEOFLASTREVISION_INVALID)
				.setMessageParam("VALUE", metadata.getDateOfLastRevision().toString())
			);
		}
		if ( metadata.getDateOfCreation() != null && ! metadata.getDateOfCreation().isValid() ){
			log.info(MARKER, "metadata.dateOfCreation : {}", metadata.getDateOfCreation());
			context.report(context.createError(CoreErrorCodes.METADATA_DATEOFCREATION_INVALID)
				.setMessageParam("VALUE", metadata.getDateOfCreation().toString())
			);
		}
	}

}
