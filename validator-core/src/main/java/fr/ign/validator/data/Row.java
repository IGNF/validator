package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * Data from a line of a table
 * 
 * @author MBorne
 *
 */
public class Row implements Validatable {

	/**
	 * Line number
	 */
	private int line;

	/**
	 * Line values
	 */
	private String[] values;

	/**
	 * Mapping with type 
	 * (Result of header validation)
	 */
	private FeatureTypeMapper mapping;

	/**
	 * 
	 * @param line
	 * @param values
	 */
	public Row(int line, String[] values, FeatureTypeMapper mapping) {
		this.line = line;
		this.values = values;
		this.mapping = mapping;
	}

	public int getLine() {
		return line;
	}

	public String[] getValues() {
		return values;
	}

	public FeatureTypeMapper getMapping() {
		return mapping;
	}

	@Override
	public void validate(Context context) {
		context.beginData(this);
		
		/*
		 * Validating attributes defined by type
		 */
		FeatureType featureType = mapping.getFeatureType() ;
		for ( int index = 0; index < featureType.getAttributeCount(); index++ ){
			AttributeType<?> attributeType = featureType.getAttribute(index);
			context.beginModel(attributeType);
			
			/*
			 * Retrieving corresponding value in table
			 */
			String inputValue = null ;
			if ( mapping.getAttributeIndex(index) >= 0 ){
				inputValue = values[ mapping.getAttributeIndex(index) ] ; 
			}

			/*
			 * Attribute validation
			 */
			Attribute<?> attribute = attributeType.newAttribute(inputValue) ;
			attribute.validate(context);

			context.endModel(attributeType);
		}
		
		context.endData(this);
	}

}
