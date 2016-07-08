package fr.ign.validator.validation;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Document;
import fr.ign.validator.model.Validator;

/**
 * 
 * Validation du nom du dossier contenant le document en fonction 
 * de l'éventuelle expression régulière.
 * 
 * @author MBorne
 *
 */
public class DocumentDirectoryNameValidator implements Validator<Document> {

	@Override
	public void validate(Context context, Document document) {
		String regexp = document.getDocumentModel().getRegexp() ;
		
		if ( regexp == null || regexp.isEmpty() ){
			return ;
		}

		if ( ! document.getDocumentPath().toURI().toString().matches(regexp) ){
			context.report(ErrorCode.DIRECTORY_UNEXPECTED_NAME);
		}
	}

}
