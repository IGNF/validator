package fr.ign.validator.validation.file.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.LanguageCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates metadata.language
 * 
 * @author MBorne
 *
 */
public class MetadataLanguageValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("MetadataLanguageValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		LanguageCode code = metadata.getMetadataLanguage() ;
		log.info(MARKER, "metadata.metadataLanguage : {}", code);
		if ( null == code ){
			context.report(
				CoreErrorCodes.METADATA_METADATALANGUAGE_NOT_FOUND
			);
		}else if ( ! code.isAllowedValue() ){
			context.report(
				CoreErrorCodes.METADATA_METADATALANGUAGE_INVALID,
				code.getValue(),
				StringUtils.join(code.getCodeList().getAllowedValues(), ", ")
			);
		}
	}

}
