package fr.ign.validator.validation.document;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Ensure that mandatory files are presents
 * 
 * @author MBorne
 *
 */
public class DocumentMandatoryFileValidator implements Validator<Document> {

	@Override
	public void validate(Context context, Document document) {
		for ( FileModel fileModel : document.getDocumentModel().getFileModels() ) {
			if ( fileModel.getMandatory().equals(FileModel.MandatoryMode.OPTIONAL) ){
				continue ;
			}
			
			if ( fileModel instanceof DirectoryModel && context.isFlatValidation() ){
				continue ;
			}
			
			if ( document.getDocumentFilesByModel(fileModel).size() >= 1 ){
				continue ;
			}
			
			context.beginModel(fileModel);
			if ( fileModel.getMandatory().equals(FileModel.MandatoryMode.WARN) ){
				context.report( 
					getErrorCodeRecommanded(fileModel),
					fileModel.getName(),
					context.getCurrentDirectory().getName()
				);
			}else{
				context.report( 
					getErrorCodeMandatory(fileModel),
					fileModel.getName(),
					context.getCurrentDirectory().getName() 
				);
			}
			context.endModel(fileModel);
		}
	}
	
	/**
	 * ErrorCode according to FileModel
	 * 
	 * Note : Did not want to explain to users that a directory is a particular file
	 * 
	 * 
	 * @param fileModel
	 * @return
	 */
	private ErrorCode getErrorCodeRecommanded(FileModel fileModel){
		if ( fileModel instanceof DirectoryModel ){
			return CoreErrorCodes.FILE_MISSING_RECOMMANDED_DIRECTORY ;
		}else{
			return CoreErrorCodes.FILE_MISSING_RECOMMANDED ;
		}
	}


	/**
	 * ErrorCode according to FileModel
	 * 
	 * @param fileModel
	 * @return
	 */
	private ErrorCode getErrorCodeMandatory(FileModel fileModel){
		if ( fileModel instanceof DirectoryModel ){
			return CoreErrorCodes.FILE_MISSING_MANDATORY_DIRECTORY ;
		}else{
			return CoreErrorCodes.FILE_MISSING_MANDATORY ;
		}
	}
}
