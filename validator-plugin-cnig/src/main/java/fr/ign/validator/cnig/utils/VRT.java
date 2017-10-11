package fr.ign.validator.cnig.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import fr.ign.validator.tools.TableReader;

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

			File vrtFile = createVRTfile( csvFile,featureType);
			return vrtFile ;
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * FIx pour generer un Vrt compatible avec ogr2ogr 1.9
	 * 
	 * @param csvFile
	 * @param featureType
	 * @return
	 * @throws IOException
	 */
	public static File createVRTfile(File csvFile, FeatureType featureType) throws Exception {
		
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
	private static List<String> getFieldNamesFromCSV(File csvFile) throws Exception {
		List<String> result = new ArrayList<String>();
		TableReader reader = TableReader.createTableReader(csvFile,StandardCharsets.UTF_8);
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
