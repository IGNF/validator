package fr.ign.validator.validation.file.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "type" is defined and allowed
 *  
 * @author MBorne
 *
 */
public class TypeValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("TypeValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		ScopeCode code = metadata.getType() ;
		log.info(MARKER, "metadata.type : {}", code);
		if ( code == null ){
			context.report(
				CoreErrorCodes.METADATA_TYPE_NOT_FOUND
			);
		}else if ( ! code.isAllowedValue() ){
			context.report(
				CoreErrorCodes.METADATA_TYPE_INVALID,
				code,
				StringUtils.join(code.getCodeList().getAllowedValues(), ", ")
			);
		}
	}

}
