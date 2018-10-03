package fr.ign.validator.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.info.DocumentInfoExtractor;
import fr.ign.validator.info.DocumentInfoWriter;
import fr.ign.validator.info.model.DocumentInfo;

/**
 * 
 * Produce a document-info.json file with various informations about the validated document
 * 
 * Note that this feature extends and replaces the previous "cnig-infos.xml" specific to validator-cnig-plugin 
 * 
 * @see DocumentInfo
 * 
 * @author CBouche
 *
 */
public class DocumentInfoExtractorPostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker POSTPROCESS_INFO_EXTRACTOR = MarkerManager.getMarker("DocumentInfoExtractorPostProcess") ;


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
		File outputInfoCnig = new File( validationDirectory, "document-info.json" ) ;
		DocumentInfoWriter infowriter = new DocumentInfoWriter() ;
		infowriter.write(documentInfo,outputInfoCnig ) ;
	}

}
