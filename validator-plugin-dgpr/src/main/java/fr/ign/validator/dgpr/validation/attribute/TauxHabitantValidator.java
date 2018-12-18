package fr.ign.validator.dgpr.validation.attribute;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class TauxHabitantValidator implements Validator<Attribute<Integer>> {

	@Override
	public void validate(Context context, Attribute<Integer> attribute) {

		if (attribute.getBindedValue() == null) {
			return;
		}

		if (attribute.getBindedValue() < 0 || attribute.getBindedValue() > 100) {
			context.report(context.createError(DgprErrorCodes.DGPR_TX_HAB_SAI_ERROR)
				.setMessageParam("VALUE", attribute.getBindedValue().toString())
			);
		}
	}

}
