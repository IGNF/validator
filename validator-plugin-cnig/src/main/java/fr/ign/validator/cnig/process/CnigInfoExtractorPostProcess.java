package fr.ign.validator.cnig.process;

import java.io.File;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.process.info.InfoExtractor;
import fr.ign.validator.cnig.process.info.InfoWriter;
import fr.ign.validator.data.DataDirectory;
import fr.ign.validator.model.Document;

/**
 * Post Traitement
 * Liste les fichiers présent dans chacun des documents
 * Extrait l'entendue géographiques des données
 * 
 * @author CBouche
 *
 */
public class CnigInfoExtractorPostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker POSTPROCESS_INFO_EXTRACTOR = MarkerManager.getMarker("POSTPROCESS_INFO_EXTRACTOR") ;
	
	/*
	 * Constante EPSG de sortie des bbox
	 */
	public static final String CRS_PROJECTION_CODE = "EPSG:4326" ;
	
	
	

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		File directory = context.getCurrentDirectory() ;
		File validationDirectory = context.getValidationDirectory() ;
		
		/*
		 * Recuperation des infos
		 */
		log.info(
			POSTPROCESS_INFO_EXTRACTOR, 
			"Extraction des informations de {} dans {}",
			directory,validationDirectory
		);

		/*
		 * Extractions des informations CNIG
		 */
		InfoExtractor infoExtractor = new InfoExtractor() ;
		DataDirectory registerRepertory = infoExtractor.parseDocument( context, document ) ;
		/*
		 * Ecriture dans le fichier infos-cnig.xml
		 */
		File outputInfoCnig = new File( validationDirectory, "infos-cnig.xml" ) ;
		InfoWriter infowriter = new InfoWriter() ;
		infowriter.write(registerRepertory,outputInfoCnig ) ;
	}
	
}
