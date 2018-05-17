package fr.ign.validator.cnig.utils;

import fr.ign.validator.cnig.utils.idurba.IdurbaHelperV1;
import fr.ign.validator.cnig.utils.idurba.IdurbaHelperV2;
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
	 * TODO get instance according to DocumentModel version (if year >= 2017, IDURBA = DocumentName)
	 * @param documentModel
	 * @return instance or null if document type if not supported
	 */
	public static IdurbaHelper getInstance(DocumentModel documentModel){
		String documentModelName = documentModel.getName();
		
		String documentType = DocumentModelNameUtils.getDocumentType(documentModelName);
		if ( documentType == null || documentType.equalsIgnoreCase("SUP") || documentType.equalsIgnoreCase("SCOT") ){
			return null;
		}

		String version = DocumentModelNameUtils.getVersion(documentModelName);
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
