package fr.ign.validator.process;

import java.io.File;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.ThreadContext;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.model.Document;


/**
 * 
 * @author CBouche
 */
public class PrepareValidationDirectory implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	private static final Marker MARKER = MarkerManager.getMarker("PREPROCESS_PREPARE_VALIDATION");
	
	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		File validationDirectory = context.getValidationDirectory() ;

		/*
		 * Controle presence d'un dossier de validation
		 */
		if ( validationDirectory.exists() ) {
			log.info(MARKER,
				"Suppression du répertoire existant {}", 
				validationDirectory.getAbsolutePath() 
			);
			FileUtils.deleteDirectory( validationDirectory );
		}
		log.info(MARKER,
			"Création du répertoire de validation {}", 
			validationDirectory.getAbsolutePath() 
		);
		validationDirectory.mkdirs() ;
		
		/*
		 * définition du chemin vers le rapport de validation
		 * TODO move to ReportBuilderLegacy
		 */
		File validationRapport = new File( validationDirectory, "validation.xml" );
		ThreadContext.put("path", validationRapport.getAbsolutePath().toString());
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception{
		
	}
}
