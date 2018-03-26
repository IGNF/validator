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
 * Ensures that "fileIdentifier" is defined and not empty
 * 
 * @author MBorne
 *
 */
public class FileIdentifierValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("FileIdentifierValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		String fileIdentifier = metadata.getFileIdentifier() ;
		log.info(MARKER, "metadata.fileIdentifier : {}", fileIdentifier);
		if ( StringUtils.isEmpty(fileIdentifier) ){
			context.report(
				CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND
			);
		}
	}

}
