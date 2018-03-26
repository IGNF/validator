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
import fr.ign.validator.metadata.Specification;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensures that "specifications" is not empty and that each "specification" is valid
 *  
 * @author MBorne
 *
 */
public class SpecificationsValidator implements Validator<Metadata> {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("LocatorsValidator") ;	
	
	@Override
	public void validate(Context context, Metadata metadata) {
		List<Specification> specifications = metadata.getSpecifications() ;
		log.info(MARKER, "metadata.specifications.size : {}", specifications.size());
		if ( specifications.isEmpty()  ){
			context.report(
				CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY
			);
		}
		int count = 1 ;
		for (Specification specification : specifications) {
			// specification.title
			if ( StringUtils.isEmpty(specification.getTitle()) ){
				context.report(
					CoreErrorCodes.METADATA_SPECIFICATION_TITLE_NOT_FOUND,
					count,
					specifications.size()
				);
			}
			
			// specification.date
			if ( specification.getDate() == null ){
				context.report(
					CoreErrorCodes.METADATA_SPECIFICATION_DATE_NOT_FOUND,
					count,
					specifications.size()
				);
			}else if ( ! specification.getDate().isValid() ){
				context.report(
					CoreErrorCodes.METADATA_SPECIFICATION_DATE_INVALID,
					count,
					specifications.size(),
					specification.getDate()
				);
			}
			
			// specification.degree
			if ( ! StringUtils.isEmpty(specification.getDegree()) ){
				if ( ! specification.getDegree().equals("true") && ! specification.getDegree().equals("false") ){
					context.report(
						CoreErrorCodes.METADATA_SPECIFICATION_DEGREE_INVALID,
						count,
						specifications.size(),
						specification.getDegree()
					);
				}
			}
			
			count++;
		}
	}


	
}
