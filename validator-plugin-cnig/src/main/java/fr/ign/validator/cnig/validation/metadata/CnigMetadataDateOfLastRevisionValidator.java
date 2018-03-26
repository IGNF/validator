package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Date;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Validates that dateOfLastRevision is defined
 * 
 * Note that CNIG DU and SUP profiles are more restrictive than INSPIRE on this point and that date content is validated in core
 * 
 * @author MBorne
 *
 */
public class CnigMetadataDateOfLastRevisionValidator implements Validator<Metadata>, ValidatorListener {

	@Override
	public void validate(Context context, Metadata metadata) {
		Date value  = metadata.getDateOfLastRevision();
		if ( value == null ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_DATEOFLASTREVISION_NOT_FOUND
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
