package fr.ign.validator.cnig.idurba;

/**
 * Decodes idUrba field
 * 
 * Format : [inseeCode or SIREN number][date of approval]
 * 
 * @warning "_" is used as a separator
 * 
 * @author MBorne
 *
 */
public abstract class IdurbaHelper {
	
	/**
	 * Validates an idUrba
	 * 
	 * @param idurba
	 * @return
	 */
	public abstract boolean isValid(String idurba);
	
	/**
	 * Validates an idUrba according to a documentName
	 * 
	 * @param idurba
	 * @param documentName
	 * @return
	 */
	public abstract boolean isValid(String idurba, String documentName);

	/**
	 * Get expected format (displayable)
	 * @return
	 */
	public abstract String getHelpFormat() ;
	
	/**
	 * Get expected IDURBA for a given documentName
	 * 
	 * @param documentName
	 * @return
	 */
	public abstract String getHelpExpected(String documentName);

}
