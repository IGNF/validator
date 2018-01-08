package fr.ign.validator.validation.document;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensure that the name of the document directory correspond the regexp defined in DocumentModel
 * 
 * @author MBorne
 *
 */
public class DocumentDirectoryNameValidator implements Validator<Document> {

	@Override
	public void validate(Context context, Document document) {
		String regexp = document.getDocumentModel().getRegexp() ;
		
		if ( StringUtils.isEmpty(regexp) ){
			return ;
		}

		String directoryName = document.getDocumentPath().getName();
		if ( ! directoryName.matches("(?i)"+regexp) ){
			context.report(CoreErrorCodes.DIRECTORY_UNEXPECTED_NAME);
		}
	}

}
