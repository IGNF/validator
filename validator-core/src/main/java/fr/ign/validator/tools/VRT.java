package fr.ign.validator.tools;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;

/**
 * Classe utilitaire pour la création de fichier VRT pour accompagner les fichiers WKT
 * @author MBorne
 *
 */
public class VRT {
	
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker VRT = MarkerManager.getMarker("VRT") ;

	/**
	 * Creation d'un VRT correspondant à sourceFile pour un FeatureType donné
	 * @param csvFile
	 * @param featureType
	 * @return 
	 */
	public static File createFile(File csvFile, FeatureType featureType){
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			//DOMSource source = new DOMSource(createDocument(csvFile,featureType));
			//File vrtFile = new File(csvFile.getParent(),FilenameUtils.getBaseName(csvFile.getName())+".vrt");
			//StreamResult result = new StreamResult(vrtFile);
			//transformer.transform(source, result);
			
			File vrtFile = createVRTfile( csvFile,featureType);
			
			return vrtFile ;
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
//		} catch (TransformerException e) {
//			throw new RuntimeException(e);
		//} catch (ParserConfigurationException e) {
		//	throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


//	private static Document createDocument(File csvFile, FeatureType featureType) throws ParserConfigurationException, IOException{
//		
//		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//		
//		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//		
//		Document doc = docBuilder.newDocument();
//		
//		// <OGRVRTDataSource>
//		Element dataSource = doc.createElement("OGRVRTDataSource");
//		doc.appendChild(dataSource);
//		
//		// <OGRVRTLayer name="remapped_layer">
//		Element layer = doc.createElement("OGRVRTLayer") ;
//		dataSource.appendChild(layer) ;
//		layer.setAttribute("name", featureType.getName());
//		
//		{
//			Element srcDataSource = doc.createElement("SrcDataSource") ;
//			srcDataSource.setTextContent(csvFile.getAbsolutePath()) ;
//			layer.appendChild(srcDataSource) ;
//		}
//		
//		// <GeometryType>
//		AttributeType<?> geometryAttribute = featureType.getAttribute("WKT") ;
//		if ( null != geometryAttribute ){
//			Element geometryType = doc.createElement("GeometryType") ;
//			geometryType.setTextContent("wkb"+geometryAttribute.getTypeName()) ;
//			layer.appendChild(geometryType) ;
//		}
//	
//		
//		/*
//		 * Each <Field> except WKT
//		 * 
//		 * Note : Cette étape est obligatoire pour que OGR2OGR ne tronquent pas
//		 *   les champs textes à 80 caractères dans les conversions en shapefile.
//		 * 
//		 */
//		List<String> fieldNames = getFieldNamesFromCSV(csvFile) ;
//		for (String fieldName : fieldNames) {
//			Element field = doc.createElement("Field") ;
//			field.setAttribute("name", fieldName);
//			field.setAttribute("type", "String");
//			field.setAttribute("width", "254");
//			layer.appendChild(field) ;
//		}
//		
//		return doc ;
//	}
	
	
	/**
	 * FIx pour generer un Vrt compatible avec ogr2ogr 1.9
	 * 
	 * @param csvFile
	 * @param featureType
	 * @return
	 * @throws IOException
	 */
	public static File createVRTfile(File csvFile, FeatureType featureType) throws IOException {
		
		File vrtFile = new File(csvFile.getParent(),FilenameUtils.getBaseName(csvFile.getName())+".vrt");
		
		
		log.info(VRT, "Ecriture Fichier vrt {} - {}",
			vrtFile.getName(),
			vrtFile.getPath()
		);
		
		FileWriter writer = new FileWriter(vrtFile);
		
		writer.append("<OGRVRTDataSource>") ; writer.append('\n') ;
		writer.append(" <OGRVRTLayer name='"+FilenameUtils.getBaseName(csvFile.getName())+"'>") ; writer.append('\n') ;
		writer.append("  <SrcDataSource>"+csvFile.getAbsolutePath()+"</SrcDataSource>") ; writer.append('\n') ;
		
		AttributeType<?> geometryAttribute = featureType.getAttribute("WKT") ;
		if ( null != geometryAttribute ){
			writer.append("  <GeometryType>"+"wkb"+geometryAttribute.getTypeName()+"</GeometryType>") ; writer.append('\n') ;
		}
			
		/*
		 * Each <Field> except WKT
		 * 
		 * Note : Cette étape est obligatoire pour que OGR2OGR ne tronquent pas
		 *   les champs textes à 80 caractères dans les conversions en shapefile.
		 * 
		 */
		List<String> fieldNames = getFieldNamesFromCSV(csvFile) ;
		for (String fieldName : fieldNames) {
			writer.append("  <Field name=\""+fieldName+"\" type=\"String\" width=\"254\" />") ; writer.append('\n') ;
		}
		
		//writer.append("  <Field name=\"fichier\" type=\"String\" width=\"254\" />") ; writer.append('\n') ;
		
		writer.append(" </OGRVRTLayer>") ; writer.append('\n') ;
		writer.append("</OGRVRTDataSource>") ; writer.append('\n') ;
		
		writer.flush();
		writer.close();
		
		return vrtFile ;
	}
	
	
	
	/**
	 * Récupère la liste des champs du CSV à l'exception de la géométrie
	 * @param csvFile
	 * @return
	 * @throws IOException
	 */
	private static List<String> getFieldNamesFromCSV(File csvFile) throws IOException{
		List<String> result = new ArrayList<String>();
		TableReader reader = new TableReader(csvFile);
		String[] header = reader.getHeader() ;
		for (String name : header) {
			if ( name.equals("WKT") ){
				continue;
			}
			result.add(name);
		}
		return result ;
	}
	
}
