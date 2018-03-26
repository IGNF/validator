package fr.ign.validator.error.format;

import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.report.ReportBuilderLegacy;

/**
 * Converts {@link ValidatorError} to String using LegacyFormat
 * 
 * TODO complete {@link LegacyFormatTest} and escape strings (convert null to "" and escape '|' in message)
 * 
 * @see ReportBuilderLegacy
 * 
 * @author MBorne
 *
 */
public class LegacyFormat {
	
	/**
	 * @param error
	 * @return
	 */
	public String write( ValidatorError error ){
		switch (error.getScope() ){
		case DIRECTORY:
			return writeDirectory(error);
		case METADATA:
			return writeMetadata(error);
		case HEADER:
			return writeHeader(error);
		case FEATURE:
			return writeFeature(error);
		default:
			return null;
		}
	}
	


	/**
	 * Directory | [code] | [level] | [file] | [message]
	 * @return
	 */
	private String writeDirectory(ValidatorError error){
		String result = "Directory" ;
		result += " | "+error.getCode() ;             // code
		result += " | "+error.getLevel() ;            // error type 
		result += " | "+error.getFile() ;             // file
		result += " | "+error.getMessage() ;          // message
		return result;
	}

	/**
	 * Metadata | [code] | [level] | [file] | [message]
	 * @return
	 */
	private String writeMetadata(ValidatorError error){
		String result = "Metadata" ;
		result += " | "+error.getCode() ;             // code
		result += " | "+error.getLevel() ;            // error type 
		result += " | "+error.getFile() ;             // file
		result += " | "+error.getMessage() ;          // message
		return result;
	}
	
	
	/**
	 * Header | [code] | [fileModel] | [level] | [column] | [value] | [modelValue] | [message]
	 * @param error
	 * @return
	 */
	private String writeHeader(ValidatorError error) {
		String result = "Header" ;
		result += " | "+error.getCode() ;              // code
		result += " | "+error.getFileModel();          // table
		result += " | "+error.getLevel() ;             // error type
		result += " | "+error.getAttribute();          // attribute (deprecated, can be found in messages) => Champ (déprécié, présent dans les messages)
		result += " | ";                               // current (deprecated, can be found in messages) =>Courant (déprécié, présent dans les messages)
		result += " | "+error.getDocumentModel();      // model
		result += " | "+error.getMessage();            // message
		return result;
	}
	

	/**
	 * Feature | [code] | [fileModel] | [level] | [column] | [line] | [currentvalue] | [type] | [model] | [message]
	 * 
	 * WARNING : [currentvalue] and [model] (size, allowed values, etc.) are deprecated and always empty
	 * 
	 * @param error
	 * @return
	 */
	private String writeFeature(ValidatorError error) {
		String result = "Feature" ;
		result += " | "+error.getCode() ;              // code
		result += " | "+error.getFileModel();          // table
		result += " | "+error.getLevel();              // error type
		result += " | "+error.getAttribute();          // attribute
		result += " | "+error.getId();                 // identifier
		result += " | ";                               // value (deprecated, can be found in messages) => valeur (déprécié, présent dans les messages)
		result += " | ";                               // type (deprecated, can be found in messages) => taille/type (déprécié, présent dans les messages)
		result += " | "+error.getDocumentModel();      // model
		result += " | "+ error.getMessage();           // message
		return result;
	}

}
