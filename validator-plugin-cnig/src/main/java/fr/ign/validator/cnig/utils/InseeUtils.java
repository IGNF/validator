package fr.ign.validator.cnig.utils;

/**
 * 
 * Validates insee codes
 * 
 * @author MBorne
 *
 */
public class InseeUtils {
	public static final String REGEXP_DEPARTEMENT = "(2A|2B|[0-9]{2,3})" ; 
	public static final String REGEXP_COMMUNE     = "(2[AB][0-9]{3}|[0-9]{5})" ; 

	/**
	 * Validates a municipality code
	 * 
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
	 * Validates a departement code
	 * 
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


