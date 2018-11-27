package fr.ign.validator.process;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.normalize.CSVNormalizer;

/**
 * 
 * Creates a DATA directory in the validation directory normalizing data from the model
 * 
 * <ul>
 * 		<li>Tables are normalized in csv according to corresponding FeatureType</li>
 * 		<li>Standard files (MetadataFiles, PDF) are copied</li>
 * 		<li>Directories are ignored</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class NormalizePostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("CreateNormalizedCsvPostProcess") ;
	

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {

	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {

	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		/*
		 * Creating DATA directory
		 */
		File dataDirectory = context.getDataDirectory() ;
		if ( ! dataDirectory.exists() ){
			dataDirectory.mkdirs() ;
		}
		
		/*
		 * Creating METADATA directory
		 */
		File metadataDirectory = context.getMetadataDirectory() ;
		if ( ! metadataDirectory.exists() ){
			metadataDirectory.mkdirs() ;
		}
		
		log.info(MARKER,"Création des fichiers normalisés dans {}",dataDirectory);
		
		/* Pour chaque fileModel, on aggrège les données dans un CSV normalisé */
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			FeatureType featureType = fileModel.getFeatureType();
			if ( featureType == null ){
				continue;
			}
			File csvFile = new File(dataDirectory, fileModel.getName()+".csv" ) ;
			CSVNormalizer normalizer = new CSVNormalizer(context, featureType, csvFile);
			List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);
			for (DocumentFile documentFile : documentFiles) {
				normalizer.append(documentFile);
			}
			normalizer.close();
		}

	}
	

}
