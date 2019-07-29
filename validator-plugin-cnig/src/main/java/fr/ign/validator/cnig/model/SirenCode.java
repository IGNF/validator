package fr.ign.validator.cnig.model;

/**
 * 
 * Validates SIREN codes (on 9 digits)
 * 
 * @see http://www.insee.fr/fr/methodes/default.asp?page=definitions/numero-siren.htm
 * 
 * @author MBorne
 *
 */
public class SirenCode {
	
	public static final String REGEXP = "[0-9]{9}" ;

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
		return siren.matches(REGEXP) ;
	}

}
