package fr.ign.validator.validation.document;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation des fichiers obligatoires dans un document
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
	 * Code d'erreur fonction du type de fichier
	 * 
	 * Remarque : Refus d'expliquer aux utilisateurs qu'un dossier est un fichier particulier
	 * 
	 * @param fileModel
	 * @return
	 */
	private ErrorCode getErrorCodeRecommanded(FileModel fileModel){
		if ( fileModel instanceof DirectoryModel ){
			return ErrorCode.FILE_MISSING_RECOMMANDED_DIRECTORY ;
		}else{
			return ErrorCode.FILE_MISSING_RECOMMANDED ;
		}
	}
	

	/**
	 * Code d'erreur fonction du type de fichier
	 * 
	 * @param fileModel
	 * @return
	 */
	private ErrorCode getErrorCodeMandatory(FileModel fileModel){
		if ( fileModel instanceof DirectoryModel ){
			return ErrorCode.FILE_MISSING_MANDATORY_DIRECTORY ;
		}else{
			return ErrorCode.FILE_MISSING_MANDATORY ;
		}
	}
}
