package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validatable;

/**
 * 
 * Les données d'une ligne d'une table
 * 
 * @author MBorne
 *
 */
public class Row implements Validatable {

	/**
	 * Le numéro de ligne
	 */
	private int line;

	/**
	 * Les valeurs de la ligne
	 */
	private String[] values;

	/**
	 * Le mapping avec un type (résultat de la validation de l'entête)
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
		 * validation des attributs définis au niveau du type
		 */
		FeatureType featureType = mapping.getFeatureType() ;
		for ( int index = 0; index < featureType.getAttributeCount(); index++ ){
			AttributeType<?> attributeType = featureType.getAttribute(index);
			context.beginModel(attributeType);
			
			// Récupération de la valeur correspondante dans la table
			String inputValue = null ;
			if ( mapping.getAttributeIndex(index) >= 0 ){
				inputValue = values[ mapping.getAttributeIndex(index) ] ; 
			}
			
			// conversion de la valeur de colonne en attribut
			Attribute<?> attribute = attributeType.newAttribute(null) ;
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
		
		context.endData(this);
	}

}
