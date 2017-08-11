package fr.ign.validator.validation.file;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Feature;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.reader.InvalidCharsetException;
import fr.ign.validator.tools.TableReader;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation d'un fichier de type table en fonction de son FeatureType
 * 
 * @author MBorne
 */
public class TableValidator implements Validator<DocumentFile> {

	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("TableValidator") ;
	
	@Override
	public void validate(Context context, DocumentFile documentFile) {
		if ( documentFile.getFileModel() instanceof TableModel ){
			TableModel tableModel = (TableModel)documentFile.getFileModel() ;
			validateFileContent(context,tableModel,documentFile.getPath()) ;
		}else{
			throw new RuntimeException(
				"Le validateur TableValidator supporte uniquement le type TableModel"
			);
		}
	}

	/**
	 * Validation d'un fichier
	 * @param context
	 * @param matchingFile
	 */
	protected void validateFileContent(Context context, TableModel tableModel, File matchingFile){
		/*
		 * Validation du fichier CSV
		 */
		log.debug(MARKER, "Lecture des données de la table {}...", matchingFile) ;
		try {
			TableReader reader;
			try {
				reader = TableReader.createTableReader(matchingFile,context.getEncoding());
			}  catch (InvalidCharsetException e) {
				log.error(MARKER, "Charset invalide détectée pour {}", matchingFile);
				context.report(
					ErrorCode.TABLE_UNEXPECTED_ENCODING,
					context.getEncoding().toString()			
				);
				log.info(MARKER, "Tentative d'autodétection de la charset pour la validation de {}",matchingFile);
				reader = TableReader.createTableReaderDetectCharset(matchingFile) ;
			}
			
			String[] header = reader.getHeader() ;
			FeatureTypeMapper mapping = validateHeader(header, tableModel, context, matchingFile) ;
			
			/*
			 * Validation des Feature
			 */
			int count = 0 ;
			while ( reader.hasNext() ){
				count++ ;
				
				// begin feature
				String featureId = ""+count ;
				context.beginData(featureId);
				String[] row = reader.next() ;
				Feature feature = validateFeature(context, mapping, row,matchingFile);
				log.trace( MARKER, "{}", feature );
				// end feature
				context.endData(featureId);
			}
			
			/*
			 * contrôle des fichiers vides
			 */
			if ( count == 0 ){
				context.report(
					ErrorCode.FILE_EMPTY,
					context.relativize(matchingFile)						
				);
			}
			
			log.info( MARKER, "{} objet validé(s)", count );
		}  catch (IOException e) {
			context.report(
				ErrorCode.FILE_NOT_OPENED,
				context.relativize(matchingFile)
			);
			return ;
		}
	}
	

	/**
	 * Validation d'une ligne
	 * @param context
	 * @param attributeIndexes
	 * @param row
	 * @return
	 */
	private Feature validateFeature(Context context, FeatureTypeMapper mapping, String[] row, File matchingFile) {
		FeatureType featureType = mapping.getFeatureType() ;
		Feature feature = featureType.newFeature() ;
		
		for ( int index = 0; index < mapping.getFeatureType().getAttributeCount(); index++ ){
			/*
			 * On récupère le type et on valide la valeur en effectuant une conversion
			 */
			AttributeType<?> attributeType = mapping.getFeatureType().getAttribute(index);
			context.beginModel(attributeType);
			
			String inputValue = null ;
			if ( mapping.getAttributeIndex(index) >= 0 ){
				inputValue = row[ mapping.getAttributeIndex(index) ] ; 
			}
			Attribute<?> attribute = feature.getAttribute(index) ;
			try {
				attribute.setValue( attributeType.bind(inputValue) );
				attribute.validate(context);
			}catch ( IllegalArgumentException e ){
				context.report(
					ErrorCode.ATTRIBUTE_INVALID_FORMAT, 
					inputValue.toString(), 
					attributeType.getTypeName()
				);
			}
			context.endModel(attributeType);
		}
		return feature ;
	}

	/**
	 * Valide l'entête et trouve la position des attributs
	 * @param header
	 * @param context
	 * @return un tableau contenant la position dans l'entête de chacun des attributs 
	 * 	décrit dans le FeatureType
	 */
	private FeatureTypeMapper validateHeader(String[] header, TableModel tableModel, Context context, File matchingFile){
		/*
		 * Position des attributs dans la table
		 */
		FeatureTypeMapper mapping = new FeatureTypeMapper(header, tableModel.getFeatureType()) ;
		
		/*
		 * L'attribut est présent dans la données mais il n'est pas défini
		 */
		for (String name : mapping.getUnexpectedAttributes()) {
			if ( name.equals("WKT") ){
				/*
				 *  On ignore les champs WKT qui sont artificiellement créé
				 *  par la conversion des dbf en CSV
				 */
				continue ;
			}
			context.report(ErrorCode.TABLE_UNEXPECTED_ATTRIBUTE, name);
		}
		
		/*
		 * L'attribut est manquant dans la donnée
		 */
		for (String name : mapping.getMissingAttributes()) {
			AttributeType<?> missingAttribute = tableModel.getFeatureType().getAttribute(name) ;
			context.beginModel(missingAttribute);
			
			if ( missingAttribute.getName().equals("WKT") ){
				context.report(
					ErrorCode.TABLE_MISSING_GEOMETRY, 
					context.relativize(matchingFile)
				);
				
			}else if ( missingAttribute.isNullable() ){
				context.report(
					ErrorCode.TABLE_MISSING_NULLABLE_ATTRIBUTE, 
					missingAttribute.getName(),
					context.relativize(matchingFile)
				);
			}else{
				context.report(
					ErrorCode.TABLE_MISSING_ATTRIBUTE, 
					missingAttribute.getName(),
					context.relativize(matchingFile)
				);
			}
			context.endModel(missingAttribute);
		}
		
		return mapping ;
	}
	
	
}
