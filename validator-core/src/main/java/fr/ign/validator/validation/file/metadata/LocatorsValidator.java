package fr.ign.validator.validation.file.metadata;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.OnlineResource;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "locators" is defined and each locator is valid
 *  
 * @author MBorne
 *
 */
public class LocatorsValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("LocatorsValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		List<OnlineResource> onlineResources = metadata.getLocators() ;
		log.info(MARKER, "metadata.locators.size : {}", onlineResources.size());
		if ( onlineResources.isEmpty()  ){
			context.report(
				CoreErrorCodes.METADATA_LOCATORS_EMPTY
			);
		}
		int count = 1 ;
		for (OnlineResource onlineResource : onlineResources) {
			if ( StringUtils.isEmpty(onlineResource.getName()) ){
				context.report(
					CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND,
					"name",
					count,
					onlineResources.size()
				);
			}
			if ( StringUtils.isEmpty(onlineResource.getProtocol()) ){
				context.report(
					CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND,
					"protocol",
					count,
					onlineResources.size()
				);
			}
			if ( StringUtils.isEmpty(onlineResource.getUrl()) ){
				context.report(
					CoreErrorCodes.METADATA_LOCATOR_URL_NOT_FOUND,
					"url",
					count,
					onlineResources.size()
				);
			}
			count++;
		}
	}

	
	
}
