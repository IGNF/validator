package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.code.ScopeCode;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Ensures that "type" is defined to "dataset" 
 *  
 * @author MBorne
 *
 */
public class CnigTypeValidator implements Validator<Metadata>, ValidatorListener {

	@Override
	public void validate(Context context, Metadata metadata) {
		ScopeCode code = metadata.getType();
		if ( code == null ){
			// already reported
			return ;
		}
		if ( ! code.equals(ScopeCode.valueOf("dataset")) && ! code.equals(ScopeCode.valueOf("serie")) ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_TYPE_INVALID,
				code.getValue()
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
