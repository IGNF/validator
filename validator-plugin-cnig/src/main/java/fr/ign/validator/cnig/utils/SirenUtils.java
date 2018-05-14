package fr.ign.validator.cnig.utils;

/**
 * 
 * Validates SIREN codes (on 9 digits)
 * 
 * @see http://www.insee.fr/fr/methodes/default.asp?page=definitions/numero-siren.htm
 * 
 * @author MBorne
 *
 */
public class SirenUtils {
	
	public static final String REGEXP_SIREN = "[0-9]{9}" ;

	/**
	 * Indicates if given parameter is a valid SIREN code
	 * 
	 * @param siren
	 * @return
	 */
	public static boolean isValid(String siren){
		if ( null == siren ){
			return false ;
		}
		return siren.matches(REGEXP_SIREN) ;
	}
	
}
