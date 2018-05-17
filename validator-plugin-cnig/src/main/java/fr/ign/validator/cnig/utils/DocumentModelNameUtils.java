package fr.ign.validator.cnig.utils;

/**
 * 
 * Parse document model name to extract informations according to naming convention :
 * 
 * cnig_{DocumentType}[_{SupCategory}]_YYYY
 * 
 * @author MBorne
 *
 */
public class DocumentModelNameUtils {

	/**
	 * Test if document model is a CNIG standard
	 * @param documentModelName
	 * @return
	 */
	public static boolean isCnigStandard(String documentModelName){
		return documentModelName.startsWith("cnig_");
	}
	
	/**
	 * Get document type from standard name (ex : PLU, SUP, etc.)
	 * @param documentModelName
	 * @return
	 */
	public static String getDocumentType(String documentModelName){
		String[] parts = documentModelName.split("_");
		if ( parts.length < 2 ){
			return null;
		}
		return parts[1];
	}

	/**
	 * Get version
	 * @param documentModelName
	 * @return
	 */
	public static String getVersion(String documentModelName){
		if ( ! isCnigStandard(documentModelName) ){
			return null;
		}
		String[] parts = documentModelName.split("_");
		String candidate = parts[parts.length - 1];
		if ( ! candidate.matches("[0-9]{4}") ){
			return null;
		}
		return candidate;
	}
}
