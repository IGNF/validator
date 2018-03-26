package fr.ign.validator.cnig.validation.metadata;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Ensures that "identifier" respect the following format : https://www.geoportail-urbanisme.gouv.fr/document/{NomDeDossier}
 * @author MBorne
 *
 */
public class CnigMetadataIdentifierValidator implements Validator<Metadata>, ValidatorListener {

	private static final String GPU_PREFIX = "https://www.geoportail-urbanisme.gouv.fr/document/";
	
	@Override
	public void validate(Context context, Metadata metadata) {
		String identifier = metadata.getIdentifier();
		if ( StringUtils.isEmpty(identifier) ){
			// already reported
			return;
		}
		String documentName = context.getCurrentDirectory().getName();
		String expectedIdentifier = GPU_PREFIX+documentName;
		if ( ! identifier.equals(expectedIdentifier) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_IDENTIFIER_INVALID, 
				identifier,
				documentName
			);
		}
	}

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
			if ( ! (fileModel instanceof MetadataModel) ){
				continue;
			}
			((MetadataModel)fileModel).addMetadataValidator(this);
		}
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}

	
}
