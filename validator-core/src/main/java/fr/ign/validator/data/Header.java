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
 * List of columns of a table associated to a FeatureType
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
	 * mapping columns/FeatureType
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
		 * Attribute in data but not defined
		 */
		for (String name : mapping.getUnexpectedAttributes()) {
			if ( name.equals("WKT") ){
				/*
				 *  Skipping "WKT" field 
				 *  (artificially created by conversion from dbf to csv)
				 */
				continue ;
			}
			context.report(CoreErrorCodes.TABLE_UNEXPECTED_ATTRIBUTE, name);
		}
		
		/*
		 * Attribute missing in data
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
