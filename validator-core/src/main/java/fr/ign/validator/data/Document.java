package fr.ign.validator.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.tools.FileUtils;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * Document materialized as a directory (documentPath) associated to a DocumentModel (documentModel)
 * 
 * @author MBorne
 *
 */
public class Document implements Validatable {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("Document") ;
	
	/**
	 * Allowed file extensions used to perform file listing
	 */
	private String[] allowedExtensions = { "dbf", "DBF", "tab", "TAB", "pdf", "PDF", "xml", "XML" , "gml" , "GML", "csv", "CSV"} ;	
	
	/**
	 * The document model
	 */
	private DocumentModel documentModel ;
	
	/**
	 * Document path (root directory for validation)
	 */
	private File documentPath ;
	
	/**
	 * Files related to Document (defined after matching step)
	 */
	private List<DocumentFile> documentFiles = new ArrayList<DocumentFile>();

	/**
	 * 
	 * @param documentModel
	 * @param path
	 */
	public Document(DocumentModel documentModel, File documentPath){
		this.documentModel = documentModel ;
		this.documentPath  = documentPath ;
	}
	

	/**
	 * @return the documentModel
	 */
	public DocumentModel getDocumentModel() {
		return documentModel;
	}

	/**
	 * @return the documentPath
	 */
	public File getDocumentPath() {
		return documentPath;
	}
	
	/**
	 * documentName calculated from directory name
	 * @return
	 */
	public String getDocumentName() {
		return documentPath.getName() ;
	}
	
	/**
	 * @return the documentFiles
	 */
	public List<DocumentFile> getDocumentFiles() {
		return documentFiles;
	}
	
	/**
	 * Retrieves documentFiles by FileModel type
	 * 
	 * Example : document.getDocumentFiles(MetadataFile.class)
	 * 
	 * @param type
	 * @return
	 */
	public <T extends FileModel > List<DocumentFile> getDocumentFiles(Class<T> type) {
		List<DocumentFile> result = new ArrayList<DocumentFile>();
		for (DocumentFile documentFile : documentFiles) {
			if ( type.isAssignableFrom( documentFile.getFileModel().getClass() ) ){
				result.add(documentFile);
			}
		}
		return result;
	}

	/**
	 * @param documentFiles the documentFiles to set
	 */
	public void removeDocumentFile(DocumentFile documentFile) {
		this.documentFiles.remove(documentFile) ;
	}

	/**
	 * Retrieve documentFiles corresponding to a model
	 * 
	 * @param fileModel
	 * @return
	 */
	public List<DocumentFile> getDocumentFilesByModel(FileModel fileModel) {
		List<DocumentFile> result = new ArrayList<DocumentFile>();
		for (DocumentFile documentFile : documentFiles) {
			if ( documentFile.getFileModel() == fileModel ){
				result.add(documentFile);
			}
		}
		return result ;
	}
	
	


	@Override
	public void validate(Context context) throws Exception {
		log.info(MARKER, "Validation de {} avec le modèle {}",
			documentPath,
			documentModel.getName()
		);

		context.setCurrentDirectory(documentPath) ;
		
		context.beginModel( documentModel ) ;
		context.beginData( this );
		
		/*
		 * calculations before matching step
		 */
		triggerBeforeMatching(context);
		
		/*
		 * matching files with model
		 */
		findFileModelForFiles( context ) ;
		
		/*
		 * executing process before validation
		 */
		triggerBeforeValidate(context);
		
		/*
		 * Validation at document level
		 */
		for ( Validator<Document> validator : documentModel.getValidators() ) {
			validator.validate(context, this);
		}
		
		/*
		 * Validation at file level
		 */
		for ( DocumentFile documentFile : documentFiles ){
			documentFile.validate(context);
		}
		
		/*
		 * executing process after validation
		 */
		triggerAfterValidate(context);
		
		context.endModel(documentModel);
		context.endData( this );
	}
	
	/**
	 * Generates event indicating file matching is starting
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void triggerBeforeMatching(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.beforeMatching(context,this);
		}
	}
	
	/**
	 * Generates event indicating validation is starting
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void triggerBeforeValidate(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.beforeValidate(context,this);
		}
	}
	
	/**
	 * Generates event indicating validation is done
	 * 
	 * @param context
	 * @throws Exception
	 */
	protected void triggerAfterValidate(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.afterValidate(context,this);
		}
	}
		
	/**
	 * Matching files in documentPath with FileModel defined in DocumentModel
	 * 
	 * @param documentPath
	 */
	public void findFileModelForFiles( Context context ){
		clearFiles() ;
		
		File documentPath = getDocumentPath() ;
		
		Collection<File> files = FileUtils.listFilesAndDirs(documentPath, allowedExtensions) ;

		/*
		 * find match with FileModel
		 */
		for (File file : files) {
			log.info(MARKER, "Recherche du FileModel pour le fichier {}...", file);
			
			FileModel fileModel = documentModel.FindFileModelByFilepath( file ) ;		
			
			if ( fileModel != null ){
				log.info(MARKER, "[MATCH]{} => {}", file, fileModel.getName());
				addDocumentFile(fileModel, file);
				continue ;
			}
			/*
			 * move elsewhere ?
			 */ 
			fileModel = documentModel.findFileModelByFilename( file ) ;
			if ( fileModel != null ){
				log.info(MARKER, "[MISPLACED_FILE]{} ~> {} (Mal placé)", file, fileModel.getName());
				
				if( context.isFlatValidation()){
					addDocumentFile(fileModel,file);
				}else{
					context.beginModel(fileModel);
					context.report(
						CoreErrorCodes.FILE_MISPLACED, 
						context.relativize(file), 
						fileModel.getName()
					);
					context.endModel(fileModel);
				}
				
				continue ;
			}
			/*
			 * not covered by model
			 */
			log.error(MARKER, "[UNEXPECTED_FILE] {} => null", file);
			context.report(
				CoreErrorCodes.FILE_UNEXPECTED, 
				context.relativize(file)
			);
		}
	}
	
	
	private void addDocumentFile(FileModel fileModel, File path){
		this.documentFiles.add(fileModel.createDocumentFile(path)) ;
	}

	/**
	 * Deletes list of files matching with model
	 */
	private void clearFiles() {
		documentFiles.clear();
	}


	
}
