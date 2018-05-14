package fr.ign.validator.validation.file.metadata;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Format;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "distributionFormats" is not empty and that formats are valid
 *  
 * @author MBorne
 *
 */
public class DistributionFormatsValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DistributionFormatsValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		List<Format> formats = metadata.getDistributionFormats();
		log.info(MARKER, "metadata.distributionFormats.size : {}", formats.size());
		if ( formats.isEmpty()  ){
			context.report(
				CoreErrorCodes.METADATA_DISTRIBUTIONFORMATS_EMPTY
			);
		}
		int count = 1 ;
		for (Format format : formats) {
			if ( StringUtils.isEmpty(format.getName()) ){
				context.report(
					CoreErrorCodes.METADATA_DISTRIBUTIONFORMAT_NAME_NOT_FOUND,
					count,
					formats.size()
				);
			}
			count++;
		}
	}

	
	
}
