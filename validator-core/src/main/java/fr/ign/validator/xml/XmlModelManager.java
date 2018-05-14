package fr.ign.validator.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;

/**
 * Bring helpers to load models from XML
 * @author MBorne
 */
public class XmlModelManager {
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("XmlModelManager") ;

	private JAXBContext context ;
	private Unmarshaller unmarshaller ;
	
	public XmlModelManager() throws JAXBException{
		this.context = JAXBContext.newInstance( FeatureType.class,DocumentModel.class ) ;
		this.unmarshaller = context.createUnmarshaller() ;
	}
	

	/**
	 * Read DocumentModel from XML file (files.xml)
	 * @param documentModelPath
	 * @return
	 * @throws JAXBException
	 */
	public DocumentModel loadDocumentModel( File documentModelPath ) throws JAXBException {
		log.info(MARKER, "Chargement du DocumentModel "+documentModelPath);
		/*
		 * loading documentModel
		 */
		DocumentModel documentModel = (DocumentModel)unmarshaller.unmarshal( documentModelPath ) ;
		if ( null == documentModel ){
			throw new RuntimeException(String.format("erreur lors du chargement de {}",documentModelPath));
		}
		/*
		 * load feature types for TableModel
		 */
		File featureTypeDirectory = new File( documentModelPath.getParent(), "types" ) ; 
		for (FileModel documentFile : documentModel.getFileModels() ) {
			if ( ! (documentFile instanceof TableModel) ){
				continue;
			}
			File featureTypeFile = new File(featureTypeDirectory, documentFile.getName()+".xml");
			FeatureType featureType = loadFeatureType(featureTypeFile);
			documentFile.setFeatureType(featureType);
		}
		return documentModel ;
	}

	/**
	 * Load FeatureType from XML file (types/[NAME].xml)
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

}
