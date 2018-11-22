package fr.ign.validator.dgpr.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validator;

public class AzimuthValidator implements Validator<Attribute<Double>>{

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("AzimuthValidator");
	
	@Override
	public void validate(Context context, Attribute<Double> validatable) {
		// TODO Auto-generated method stub
		Row row = context.getDataByType(Row.class);
		String[] values = row.getValues();
		FeatureTypeMapper mapping = row.getMapping();
		FeatureType featureType = mapping.getFeatureType() ;

		for (int i = 0; i < featureType.getAttributeCount(); i++) {
			AttributeType<?> attributeType = featureType.getAttribute(i);
			if (attributeType.getName().equals("VITESSE")) {
				//ici la valeur de VITESSE est récupérée
				String value = values[mapping.getAttributeIndex(i)];
			}	
		}
		
		
		
	}

}
