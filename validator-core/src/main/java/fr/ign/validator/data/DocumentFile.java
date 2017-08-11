package fr.ign.validator.data;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * Représente un fichier associé à un modèle
 */
public class DocumentFile implements Validatable {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DocumentFile") ;

	/**
	 * Le modèle de fichier
	 */
	private FileModel fileModel ;
	
	/**
	 * Le chemin vers le fichier
	 */
	private File path ;

	/**
	 * Construction à partir d'un modèle et d'un chemin
	 * @param fileModel
	 * @param path
	 */
	public DocumentFile(FileModel fileModel, File path){
		this.fileModel = fileModel ;
		this.path = path ;
	}


	/**
	 * @return the fileModel
	 */
	public FileModel getFileModel() {
		return fileModel;
	}

	/**
	 * @return the path
	 */
	public File getPath() {
		return path;
	}

	/**
	 * Validation du FileModel
	 * @param context
	 */
	public final void validate(Context context) {
		/*
		 * Validation du fichier
		 */
		log.debug(MARKER, "Validation du fichier {} avec le modèle {}...", path, fileModel.getName()) ;
		context.beginModel(fileModel);
		context.beginData( context.relativize(path) );
		for (Validator<DocumentFile> validator : fileModel.getValidators()) {
			validator.validate(context, this);
		}
		context.endData( context.relativize(path) );
		context.endModel(fileModel);
	}
	
}
