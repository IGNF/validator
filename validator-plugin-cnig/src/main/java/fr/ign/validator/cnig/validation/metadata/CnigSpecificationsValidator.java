package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.utils.SpecificationUtils;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Ensures that "specifications" contains an element with the following form : 
 * 
 * ex : "CNIG PLU v2013"
 * 
 *  
 * @author MBorne
 *
 */
public class CnigSpecificationsValidator implements Validator<Metadata>, ValidatorListener {

	@Override
	public void validate(Context context, Metadata metadata) {
		Specification specification = SpecificationUtils.findCnigSpecification(metadata);
		if ( specification == null ){
			context.report(
				CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND
			);
			return ;
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
