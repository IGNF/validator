package fr.ign.validator.process;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;
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
		List<DocumentFile> documentFiles = document.getDocumentFiles() ;
		for (DocumentFile documentFile : documentFiles) {
			File srcFile = documentFile.getPath() ;
			FileModel fileModel = documentFile.getFileModel() ;
			
			if ( fileModel instanceof TableModel ){
				/*
				 * Creating standardized csv file
				 */
				File csvFile = new File(dataDirectory, fileModel.getName()+".csv" ) ;
				{
					log.info(MARKER,"Normalisation de {} en {}...",srcFile, csvFile);
					CSVNormalizer normalizer = new CSVNormalizer(context,fileModel.getFeatureType());
					normalizer.normalize(srcFile, context.getEncoding(), context.getCoordinateReferenceSystem(), csvFile);
				}
			}else if ( fileModel instanceof PdfModel ){
				File destFile = new File(dataDirectory, srcFile.getName()) ;
				log.info(MARKER,"Copie de {} dans {}...",srcFile, destFile);
				FileUtils.copyFile(srcFile, destFile);
			}else if ( fileModel instanceof MetadataModel ){
				File destFile = new File(metadataDirectory, srcFile.getName()) ;
				log.info(MARKER,"Copie de {} dans {}...",srcFile, destFile);
				FileUtils.copyFile(srcFile, destFile);
			}
		}
	}
	

}
