package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.ValidatorListener;

/**
 *
 */
public class SomeActionPostProcess implements ValidatorListener {

  public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("POSTPROCESS_SOMEACTION");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		log.error(MARKER,
			"Some error is trigger (beforeMatching) in SomeActionPostProcess (custom PostProcess)"
		);
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		log.error(MARKER,
			"Some error is trigger (beforeValidate) in SomeActionPostProcess (custom PostProcess)"
		);
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		log.error(MARKER,
			"Some error is trigger (afterValidate) in SomeActionPostProcess (custom PostProcess)"
		);
	}

}
