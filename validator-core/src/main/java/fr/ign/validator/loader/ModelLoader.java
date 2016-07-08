package fr.ign.validator.loader;

import java.io.File;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureCatalogue;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;

/**
 * 
 * Charge une configuration données par un dossier contenant :
 * - files.xml : un fichier de mapping
 * - types : un dossier contenant des FeatureType décrivant des modèles de table
 * 
 * @author MBorne
 */
public class ModelLoader {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("LOADER") ;

	
	private JAXBContext context ;
	private Unmarshaller unmarshaller ;
	
	public ModelLoader() throws JAXBException{
		this.context = JAXBContext.newInstance( FeatureType.class,DocumentModel.class ) ;
		this.unmarshaller = context.createUnmarshaller() ;
	}
	
	
	/**
	 * Charge un FeatureType à partir d'un fichier XML
	 * @param path
	 * @return
	 * @throws JAXBException
	 */
	public FeatureType loadFeatureType( File path ) throws JAXBException{
		log.info(MARKER, "Chargement du FeatureType "+path);
		FeatureType featureType = (FeatureType)unmarshaller.unmarshal(path) ;
		/*
		 * check typeName = fileName
		 */
		if ( ! FilenameUtils.getBaseName(path.getPath()).equals( featureType.getTypeName() ) ) {
			String message = String.format( "typeName != fileName pour %1s", path.getName() ) ;
			log.error( MARKER, message );
			throw new RuntimeException(message) ;
		}
		return featureType ;
	}
	
	/**
	 * Chargement de tous les FeatureType se situant dans un dossier
	 * @param featureTypeDirectory
	 * @return
	 * @throws JAXBException
	 */
	public FeatureCatalogue loadFeatureCatalogue( File featureTypeDirectory ) throws JAXBException{
		FeatureCatalogue featureCatalogue = new FeatureCatalogue() ;
		if ( featureTypeDirectory.exists() ){
			String[] extensions = {"xml"};
			@SuppressWarnings("unchecked")
			Collection<File> featureTypePaths = FileUtils.listFiles(featureTypeDirectory, extensions, false) ;
			for (File featureTypePath : featureTypePaths) {
				FeatureType featureType = loadFeatureType(featureTypePath);
				featureCatalogue.addFeatureType(featureType);
			}
		}else{
			log.warn(MARKER, "Le dossier types est absent pour le documentModel");
		}
		return featureCatalogue ;
	}
	

	/**
	 * Lit un modèle de document à partir d'un fichier XML (files.xml)
	 * @param documentModelPath
	 * @return
	 * @throws JAXBException
	 */
	public DocumentModel loadDocumentModel( File documentModelPath ) throws JAXBException {
		log.info(MARKER, "Chargement du DocumentModel "+documentModelPath);
		/*
		 * chargement du modèle de document
		 */
		DocumentModel documentModel = (DocumentModel)unmarshaller.unmarshal( documentModelPath ) ;
		if ( null == documentModel ){
			throw new RuntimeException(String.format("erreur lors du chargement de {}",documentModelPath));
		}
		/*
		 * Chargement des FeatureType
		 */
		File featureTypeDirectory = new File( documentModelPath.getParent(), "types" ) ; 
		FeatureCatalogue featureCatalogue = loadFeatureCatalogue(featureTypeDirectory) ;
		documentModel.setFeatureCatalogue(featureCatalogue);
		
		/*
		 * Assignation des FeatureTypes aux TableFile
		 */
		for (FileModel documentFile : documentModel.getFileModels() ) {
			if ( documentFile instanceof TableModel ){
				FeatureType featureType = featureCatalogue.getFeatureTypeByName( documentFile.getName() ) ;
				documentFile.setFeatureType(featureType);
			}
		}
		return documentModel ;
	}
}
