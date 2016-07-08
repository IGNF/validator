package fr.ign.validator.utils;

/**
 * 
 * Utilitaire de validation des codes INSEE
 * 
 * @author MBorne
 *
 */
public class InseeUtils {
	public static final String REGEXP_DEPARTEMENT = "(2A|2B|[0-9]{2,3})" ; 
	public static final String REGEXP_COMMUNE     = "(2[AB][0-9]{3}|[0-9]{5})" ; 

	/**
	 * Validation d'un code de commune
	 * @param insee
	 * @return
	 */
	public static boolean isValidCommune(String insee){
		if ( null == insee ){
			return false ;
		}
		return insee.matches(REGEXP_COMMUNE) ;
	}
	
	/**
	 * Validation d'un code de département
	 * @param insee
	 * @return
	 */
	public static boolean isValidDepartement(String insee){
		if ( null == insee ){
			return false ;
		}
		return insee.matches(REGEXP_DEPARTEMENT) ;
	}
	
	
	
}


