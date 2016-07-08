package fr.ign.validator.utils;

/**
 * 
 * Utilitaire pour la validation des codes SIREN codé sur 9 chiffres
 * 
 * @see http://www.insee.fr/fr/methodes/default.asp?page=definitions/numero-siren.htm
 * 
 * @author MBorne
 *
 */
public class SirenUtils {
	
	public static final String REGEXP_SIREN = "[0-9]{9}" ;

	/**
	 * Indique si le paramètre siren est un code SIREN valide
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
