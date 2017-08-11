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
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.tools.FileUtils;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Représente un document matérialisé par un chemin vers un dossier (documentPath)
 *  et un modèle (documentModel)
 * 
 * @author MBorne
 *
 */
public class Document implements Validatable {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("Document") ;
	
	/**
	 * La liste des extensions de fichier à valider
	 */
	private String[] allowedExtensions = { "dbf", "DBF", "tab", "TAB", "pdf", "PDF", "xml", "XML" , "gml" , "GML", "csv", "CSV"} ;	
	
	/**
	 * Le modèle de document
	 */
	private DocumentModel documentModel ;
	
	/**
	 * Validation d'un document
	 */
	private File documentPath ;
	
	/**
	 * Les fichiers du document (définit après l'étape de matching)
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
	 * Le nom du document calculé à partir du nom de dossier
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
	 * Recherche des DocumentFile par type de FileModel
	 * 
	 * Exemple : document.getDocumentFiles(MetadataFile.class)
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
	 * Fonction utilitaire permettant de récupérer les DocumentFile correspondant
	 *  à un modèle
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
		context.beginData( documentPath.getName() );
		
		/*
		 * traitements avant matching
		 */
		triggerBeforeMatching(context);
		
		/*
		 * Matching des fichiers avec les définitions de fichiers
		 */
		findFileModelForFiles( context ) ;
		
		/*
		 * Exécution des traitements beforeValidate
		 */
		triggerBeforeValidate(context);
		
		/**
		 * Validation au niveau du document
		 */
		for ( Validator<Document> validator : documentModel.getValidators() ) {
			validator.validate(context, this);
		}
		
		/**
		 * Validation au niveau des fichiers (traverse)
		 */
		for ( DocumentFile documentFile : documentFiles ){
			documentFile.validate(context);
		}
		
		/*
		 * Exécution des post-traitements
		 */
		triggerAfterValidate(context);
		
		context.endModel(documentModel);
		context.endData( documentPath.getName() );
	}
	
	/**
	 * Envoi l'événement indiquant que la mise en correspondance des fichiers va commencer
	 * @param context
	 * @throws Exception
	 */
	protected void triggerBeforeMatching(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.beforeMatching(context,this);
		}
	}
	
	/**
	 * Envoi l'événement indiquant que la validation va commencer
	 * @param context
	 * @throws Exception
	 */
	protected void triggerBeforeValidate(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.beforeValidate(context,this);
		}
	}
	
	/**
	 * Envoi l'événement indiquant que la validation du document est terminée
	 * @param context
	 * @throws Exception
	 */
	protected void triggerAfterValidate(Context context) throws Exception {
		for (ValidatorListener validatorListener : context.getValidatorListeners()) {
			validatorListener.afterValidate(context,this);
		}
	}
		
	/**
	 * Mise en correspondance des fichiers présents dans documentPath avec les FileModel définit
	 *  dans le modèle de document.
	 * @param documentPath
	 */
	public void findFileModelForFiles( Context context ){
		clearFiles() ;
		
		File documentPath = getDocumentPath() ;
		
		Collection<File> files = FileUtils.listFilesAndDirs(documentPath, allowedExtensions) ;

		/*
		 * recherche des correspondances avec les FileModel
		 */
		for (File file : files) {
			log.info(MARKER, "Recherche du FileModel pour le fichier {}...", file);
			
			FileModel fileModel = documentModel.getMatchingFileModelByPath( documentPath, file ) ;		
			
			if ( fileModel != null ){
				log.info(MARKER, "[MATCH]{} => {}", file, fileModel.getName());
				addDocumentFile(fileModel, file);
				continue ;
			}
			/*
			 * mal placé?
			 */ 
			fileModel = documentModel.getMatchingFileModelByName( documentPath, file ) ;
			if ( fileModel != null ){
				log.info(MARKER, "[MISPLACED_FILE]{} ~> {} (Mal placé)", file, fileModel.getName());
				
				if( context.isFlatValidation()){
					addDocumentFile(fileModel,file);
				}else{
					context.beginModel(fileModel);
					context.report(
						ErrorCode.FILE_MISPLACED, 
						context.relativize(file), 
						fileModel.getName()
					);
					context.endModel(fileModel);
				}
				
				continue ;
			}
			/*
			 * non prévu dans le modèle
			 */
			log.error(MARKER, "[UNEXPECTED_FILE] {} => null", file);
			context.report(
				ErrorCode.FILE_UNEXPECTED, 
				context.relativize(file)
			);
		}
	}
	
	
	private void addDocumentFile(FileModel fileModel, File path){
		this.documentFiles.add(new DocumentFile(fileModel, path)) ;
	}
	
	/**
	 * Supprime la liste des fichiers mis en correspondance avec le modèle
	 */
	private void clearFiles() {
		documentFiles.clear();
	}




	
}
