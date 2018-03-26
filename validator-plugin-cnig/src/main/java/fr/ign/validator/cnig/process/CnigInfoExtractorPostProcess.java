package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.info.DocumentInfo;
import fr.ign.validator.cnig.info.DocumentInfoExtractor;
import fr.ign.validator.cnig.info.DocumentInfoWriter;
import fr.ign.validator.data.Document;

/**
 * 
 * Post process
 * Lists found files in each document
 * Extracts geographical data extent
 * 
 * @author CBouche
 *
 */
public class CnigInfoExtractorPostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker POSTPROCESS_INFO_EXTRACTOR = MarkerManager.getMarker("POSTPROCESS_INFO_EXTRACTOR") ;
	
	/*
	 * EPSG output for bbox (constant)
	 */
	public static final String CRS_PROJECTION_CODE = "EPSG:4326" ;
	
	
	

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		File directory = context.getCurrentDirectory() ;
		File validationDirectory = context.getValidationDirectory() ;
		
		/*
		 * Getting infos
		 */
		log.info(
			POSTPROCESS_INFO_EXTRACTOR, 
			"Extraction des informations de {} dans {}",
			directory,validationDirectory
		);

		/*
		 * Extracting cnig informations
		 */
		DocumentInfoExtractor infoExtractor = new DocumentInfoExtractor() ;
		DocumentInfo documentInfo = infoExtractor.parseDocument( context, document ) ;
		/*
		 * Writing in infos-cnig.xml file
		 */
		File outputInfoCnig = new File( validationDirectory, "infos-cnig.xml" ) ;
		DocumentInfoWriter infowriter = new DocumentInfoWriter() ;
		infowriter.write(documentInfo,outputInfoCnig ) ;
	}
	
}
