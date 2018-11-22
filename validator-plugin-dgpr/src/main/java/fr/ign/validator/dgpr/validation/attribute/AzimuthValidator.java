package fr.ign.validator.dgpr.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validator;

public class AzimuthValidator implements Validator<Attribute<Double>>{

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("AzimuthValidator");
	
	@Override
	public void validate(Context context, Attribute<Double> validatable) {

		Row row = context.getDataByType(Row.class);
		String[] values = row.getValues();
		FeatureTypeMapper mapping = row.getMapping();
		FeatureType featureType = mapping.getFeatureType() ;

		if (validatable.getBindedValue() != null) {
			return;
		}
		// si je suis null alors je doit verifié que le débit et la vitesse sont null
		String valueVitesse = "";
		String valueDeblin = "";
		for (int i = 0; i < featureType.getAttributeCount(); i++) {
			AttributeType<?> attributeType = featureType.getAttribute(i);
			if (attributeType.getName().equals("VITESSE")) {
				valueVitesse = values[mapping.getAttributeIndex(i)];
			}
			if (attributeType.getName().equals("DEBLIN")) {
				valueDeblin = values[mapping.getAttributeIndex(i)];
			}
		}
		if (valueDeblin != null || valueVitesse != null) {
			context.report(context.createError(DgprErrorCodes.DGPR_AZIMUTH_ERROR)
					.setMessageParam("VALUE_VITESSE", valueVitesse)
					.setMessageParam("VALUE_DEBLIN", valueDeblin)
			);
		}

	}

}
