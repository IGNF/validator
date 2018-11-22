package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validator;

public class DebLinMaxValidator implements Validator<Attribute<Double>> {

	@Override
	public void validate(Context context, Attribute<Double> attribute) {

		Row row = context.getDataByType(Row.class);

		FeatureTypeMapper mapping = row.getMapping();		

		String[] values = row.getValues();
		
		FeatureType featureType = mapping.getFeatureType() ;
		
		Double deblin_min = null;
		//on récupère l'attribut DEBLIN_MIN
		for (int i = 0; i < featureType.getAttributeCount(); i++) {
			AttributeType<?> attributeType = featureType.getAttribute(i);
			if (!attributeType.getName().equals("DEBLIN_MIN")) {
				continue;
			}
			if (mapping.getAttributeIndex(i) >= 0) {
				deblin_min = Double.parseDouble(values[mapping.getAttributeIndex(i)]);          
			}
		}
		
		//l'attribut DEBLIN_MAX doit être null ou supérieur à DEBLIN_MIN
		if (attribute.getBindedValue() == null || attribute.getBindedValue() > deblin_min) {
			return;
		}

		context.report(context.createError(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR)
				.setMessageParam("VALUE_MAX", attribute.getBindedValue().toString())
				.setMessageParam("VALUE_MIN", deblin_min.toString())
		);
	}

}
