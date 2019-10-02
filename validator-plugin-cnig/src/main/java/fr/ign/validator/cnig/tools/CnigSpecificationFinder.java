package fr.ign.validator.cnig.tools;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.model.DocumentType;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

/**
 * Extract CNIG specification from metadata
 * @author MBorne
 *
 */
public class CnigSpecificationFinder {
	
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("CnigSpecificationFinder") ;	
	
	/**
	 * Get regexp corresponding to "CNIG {DocumentType} v{YYYY}"
	 * @return
	 */
	public static String getRegexpTitle() {
		return "(?i)CNIG\\s("+Arrays.stream(DocumentType.values())
			.map(t -> t.toString())
			.collect(Collectors.joining("|"))
		+")\\sv\\d{4}";
	}

	/**
	 * Find specification definition according to CNIG DU or SUP profile
	 * @param metadata
	 * @return
	 */
	public static Specification findCnigSpecification(Metadata metadata){
		for ( Specification specification : metadata.getSpecifications() ){
			/* ignore specification where dateType != publication */
			if ( StringUtils.isEmpty(specification.getDateType()) || ! specification.getDateType().equals("publication") ){
				log.warn(MARKER, "ignore specification with title={} (dateType = {}, should be publication)",specification.getTitle(), specification.getDateType());
				continue;
			}
			if ( isCnigSpecification(specification.getTitle()) ){
				return specification;				
			}
		}
		return null;
	}
	
	/**
	 * Test if specification title
	 * @param specification
	 * @return
	 */
	public static boolean isCnigSpecification(String title){
		if ( StringUtils.isEmpty(title) ){
			log.info(MARKER,"isCnigSpecification({}) : false (empty title)",title);
			return false;
		}
		String regexpTitle = getRegexpTitle();
		boolean result = title.matches(regexpTitle) ;
		log.info(MARKER,
			"isCnigSpecification({}) : {} (regexp : \"{}\")",
			title,
			result,
			regexpTitle
		);
		return result;
	}
	
}
