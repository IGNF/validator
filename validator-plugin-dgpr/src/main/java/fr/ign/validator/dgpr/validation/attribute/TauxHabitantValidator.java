package fr.ign.validator.dgpr.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class TauxHabitantValidator implements Validator<Attribute<Integer>> {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TauxHabitantValidator");

	@Override
	public void validate(Context context, Attribute<Integer> attribute) {
		Integer value = attribute.getBindedValue();
		if (value == null) {
			return;
		}
		if (value < 0 || value > 100) {
			context.report(context.createError(DgprErrorCodes.DGPR_TX_HAB_SAI_ERROR)
				.setMessageParam("VALUE", String.valueOf(value))
			);
		}
	}

}
