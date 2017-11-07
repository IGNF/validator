package fr.ign.validator.data;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.Validatable;

/**
 * Représente un fichier associé à un modèle
 */
public abstract class DocumentFile implements Validatable {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("DocumentFile") ;

	
	/**
	 * Le chemin vers le fichier
	 */
	private File path ;

	/**
	 * Construction à partir d'un modèle et d'un chemin
	 * @param fileModel
	 * @param path
	 */
	protected DocumentFile(File path){
		this.path = path ;
	}


	/**
	 * @return the fileModel
	 */
	abstract public FileModel getFileModel() ;

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
		log.debug(MARKER, "Validation du fichier {} avec le modèle {}...", path, getFileModel().getName()) ;
		context.beginModel(getFileModel());
		context.beginData( this );
		validateContent(context);
		context.endData( this );
		context.endModel(getFileModel());
	}

	/**
	 * Validate file content
	 * @param context
	 */
	abstract protected void validateContent(Context context) ;
	
}
