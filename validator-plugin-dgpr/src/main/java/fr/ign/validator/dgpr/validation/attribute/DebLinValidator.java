package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class DebLinValidator implements Validator<Attribute<Double>> {

	@Override
	public void validate(Context context, Attribute<Double> attribute) {

		//l'attribut DEBLIN doit être null ou supérieur à 0
		if (attribute.getBindedValue() == null) {
			return;
		}
		if (attribute.getBindedValue() > 0) {
			return;
		}
		
		context.report(context.createError(DgprErrorCodes.DGPR_DEBLIN_ERROR)
				.setMessageParam("VALUE_MIN", attribute.getBindedValue().toString())
		);
	}

}
