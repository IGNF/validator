package fr.ign.validator.cnig.model;

/**
 * 
 * Validates insee codes
 * 
 * @author MBorne
 *
 */
public class MunicipalityCode {
	public static final String REGEXP = "(2[AB][0-9]{3}|[0-9]{5})" ; 

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
		return insee.matches(REGEXP) ;
	}

}


