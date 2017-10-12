package fr.ign.validator.data;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * La liste des colonnes des tables associée à un FeatureType
 * 
 * @author MBorne
 *
 */
public class Header implements Validatable {

	/**
	 * TODO rely on Context
	 */
	private File matchingFile ;

	/**
	 * Le mapping colonnes/FeatureType
	 */
	private FeatureTypeMapper mapping ;
	
	/**
	 * @param columns
	 * @param mapping
	 */
	public Header(File matchingFile, FeatureTypeMapper mapping){
		this.matchingFile = matchingFile;
		this.mapping = mapping ;
	}

	@Override
	public void validate(Context context) {
		context.beginData(this);
		
		FeatureType featureType = mapping.getFeatureType();
		
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
			context.report(CoreErrorCodes.TABLE_UNEXPECTED_ATTRIBUTE, name);
		}
		
		/*
		 * L'attribut est manquant dans la donnée
		 */
		for (String name : mapping.getMissingAttributes()) {
			AttributeType<?> missingAttribute = featureType.getAttribute(name) ;
			context.beginModel(missingAttribute);
			
			if ( missingAttribute.getName().equals("WKT") ){
				context.report(
					CoreErrorCodes.TABLE_MISSING_GEOMETRY, 
					context.relativize(matchingFile)
				);
				
			}else if ( missingAttribute.isNullable() ){
				context.report(
					CoreErrorCodes.TABLE_MISSING_NULLABLE_ATTRIBUTE, 
					missingAttribute.getName(),
					context.relativize(matchingFile)
				);
			}else{
				context.report(
					CoreErrorCodes.TABLE_MISSING_ATTRIBUTE, 
					missingAttribute.getName(),
					context.relativize(matchingFile)
				);
			}
			context.endModel(missingAttribute);
		}

		context.endData(this);
	}

	
	
}
