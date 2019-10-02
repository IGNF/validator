package fr.ign.validator.cnig.tools;

import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.model.DocumentModel;

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
	 * Get implementation according to document model
	 * @param documentModel
	 * @return
	 */
	public static IdurbaHelper getInstance(DocumentModel documentModel){
		String documentModelName = documentModel.getName();
		
		String documentType = DocumentModelName.getDocumentType(documentModelName);
		if ( documentType == null || documentType.equalsIgnoreCase("SUP") || documentType.equalsIgnoreCase("SCOT") ){
			return null;
		}
	
		String version = DocumentModelName.getVersion(documentModelName);
		if ( version.equals("2013") || version.equals("2014") ){
			return new IdurbaHelperV1();
		}else {
			return new IdurbaHelperV2();
		}
	}

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
