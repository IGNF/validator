package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validator;

public class VitesseMinValidator implements Validator<Attribute<Double>> {

	@Override
	public void validate(Context context, Attribute<Double> attribute) {

		Row row = context.getDataByType(Row.class);

		FeatureTypeMapper mapping = row.getMapping();		

		String[] values = row.getValues();

		//Stocke la vitesse max et la vitesse min en string
		Double db_vit_max = null;
		Double db_vit_min = attribute.getBindedValue();	

		FeatureType featureType = mapping.getFeatureType() ;

		for (int i = 0; i < featureType.getAttributeCount(); i++) {
			AttributeType<?> attributeType = featureType.getAttribute(i);
			if (!attributeType.getName().equals("VITESS_MAX")) {
				continue;
			}
			if (mapping.getAttributeIndex(i) >= 0 && values[mapping.getAttributeIndex(i)] != null) {
				db_vit_max = Double.parseDouble(values[mapping.getAttributeIndex(i)]);          
			}
		}

		// Si la vitesse max est null
		// ou la vitesse max est inferieur a la vitesse min
		// , le test est validé
		if (db_vit_max == null
			|| db_vit_min < db_vit_max
		) {
			return;
		}

		context.report(context.createError(DgprErrorCodes.DGPR_VITESSE_MIN_ERROR)
				.setMessageParam("VALUE_MIN", db_vit_min.toString())
				.setMessageParam("VALUE_MAX", db_vit_max.toString())
		);
	}

}
