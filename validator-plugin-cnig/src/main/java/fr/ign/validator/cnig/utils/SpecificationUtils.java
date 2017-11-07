package fr.ign.validator.cnig.utils;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;

/**
 * Extract CNIG specification from metadata
 * @author MBorne
 *
 */
public class SpecificationUtils {
	
	private static final String CNIG_SPECIFICATION_REGEXP = "(?i)CNIG\\s(CC|POS|PLU|PLUi|PSMV|SUP)\\sv\\d{4}";
	
	public static class CnigSpecificationInfo {
		public String documentType;
		public String version ;
	}

	/**
	 * Find specification definition according to CNIG DU or SUP profile
	 * @param metadata
	 * @return
	 */
	public static Specification findCnigSpecification(Metadata metadata){
		for ( Specification specification : metadata.getSpecifications() ){
			if ( isCnigSpecification(specification) ){
				return specification;				
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param specification
	 * @return
	 */
	public static boolean isCnigSpecification(Specification specification){
		if ( StringUtils.isEmpty(specification.getDateType()) || ! specification.getDateType().equals("publication") ){
			return false;
		}
		String title = specification.getTitle();
		if ( StringUtils.isEmpty(title) ){
			return false;
		}
		return title.matches(CNIG_SPECIFICATION_REGEXP) ;
	}

	/**
	 * 
	 * @param specification
	 * @return
	 */
	public static CnigSpecificationInfo parseCnigSpecification(Specification specification){
		String[] parts = specification.getTitle().split(" ");
		CnigSpecificationInfo result = new CnigSpecificationInfo();
		result.documentType = parts[1] ;
		result.version = parts[2].replaceAll("v", "");
		return result;
	}
	
}
