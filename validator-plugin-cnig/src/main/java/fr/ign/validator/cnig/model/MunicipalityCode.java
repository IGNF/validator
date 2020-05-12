package fr.ign.validator.cnig.model;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Validates INSEE codes for municipalities
 * 
 * @author MBorne
 *
 */
public class MunicipalityCode {
	public static final String REGEXP = "(2[AB][0-9]{3}|[0-9]{5})" ; 

	/**
	 * Validates a municipality code
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isValid(String value){
		if ( StringUtils.isEmpty(value) ){
			return false ;
		}
		return value.matches(REGEXP) ;
	}

}


