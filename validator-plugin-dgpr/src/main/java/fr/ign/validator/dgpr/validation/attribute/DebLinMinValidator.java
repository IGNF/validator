package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class DebLinMinValidator implements Validator<Attribute<Double>> {

	@Override
	public void validate(Context context, Attribute<Double> attribute) {

		//Stocke la vitesse max et la vitesse min en string

		System.out.println(attribute.getBindedValue());

		if (attribute.getBindedValue() > 0) {
			return;
		}

		context.report(context.createError(DgprErrorCodes.DGPR_DEBLIN_MIN_ERROR)
				.setMessageParam("VALUE_MIN", attribute.getBindedValue().toString())
		);
	}

}
