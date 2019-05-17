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
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.normalize.CSVNormalizer;

/**
 * 
 * Creates DATA and METADATA directories in the validation directory :
 * 
 * <ul>
 * 		<li>Tables are normalized as csv files according to corresponding FeatureType in DATA directory</li>
 * 		<li>PDF are copied to DATA directory</li>
 * 		<li>Metadata are copied to METADATA directory</li> 
 * 		<li>Directories are ignored</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class NormalizePostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("NormalizePostProcess") ;
	

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
		
		log.info(MARKER,"Create normalized files in {}",dataDirectory);
		
		/* Pour chaque fileModel, on aggrège les données dans un CSV normalisé */
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			if ( fileModel instanceof TableModel ){
				FeatureType featureType = fileModel.getFeatureType();
				if ( featureType == null ){
					continue;
				}
				File csvFile = new File(dataDirectory, fileModel.getName()+".csv" ) ;
				CSVNormalizer normalizer = new CSVNormalizer(context, featureType, csvFile);
				List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);
				for (DocumentFile documentFile : documentFiles) {
					log.info(MARKER,"Append {} to CSV file {}...",documentFile.getPath(), csvFile);
					normalizer.append(documentFile.getPath());
				}
				normalizer.close();
			}else if ( fileModel instanceof PdfModel ){
				List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);
				for (DocumentFile documentFile : documentFiles) {
					File srcFile = documentFile.getPath();
					File destFile = new File(dataDirectory, srcFile.getName()) ;
					log.info(MARKER,"Copy {} to {}...",srcFile, destFile);
					FileUtils.copyFile(srcFile, destFile);
				}
			}else if ( fileModel instanceof MetadataModel ){
				List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);
				for (DocumentFile documentFile : documentFiles) {
					File srcFile = documentFile.getPath();
					File destFile = new File(metadataDirectory, srcFile.getName()) ;
					log.info(MARKER,"Copy {} to {}...",srcFile, destFile);
					FileUtils.copyFile(srcFile, destFile);
				}
			}
		}

	}
	

}
