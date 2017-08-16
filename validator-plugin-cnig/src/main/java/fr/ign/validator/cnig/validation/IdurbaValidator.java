package fr.ign.validator.cnig.validation;

import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.utils.IdurbaUtils;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation des codes IDURBA dans la table 
 * 
 * @author MBorne
 *
 */
public class IdurbaValidator implements Validator<Attribute<String>>, ValidatorListener {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		if ( ! IdurbaUtils.isValid(attribute.getValue()) ){
			context.report(
				ErrorCode.CNIG_IDURBA_MALFORMED, 
				attribute.getValue()
			);
		}
	}

	/**
	 * Extension de la validation de la table DOC_URBA
	 * @param context
	 * @param document
	 * @throws Exception
	 */
	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		/*
		 * Ajout de ce validateur sur DOC_URBA.IDURBA
		 */
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			if ( fileModel.getName().equals("DOC_URBA") ){
				AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("IDURBA") ;
				if ( attribute instanceof StringType ){
					((StringType)attribute).addValidator(this);
				}else{
					throw new RuntimeException("IDURBA de DOC_URBA n'est pas configuré comme étant une chaîne de caractère");
				}				
			}
		}
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}

}
