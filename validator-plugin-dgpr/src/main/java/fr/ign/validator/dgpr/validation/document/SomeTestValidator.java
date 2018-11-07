package fr.ign.validator.dgpr.validation.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 *
 */
public class SomeTestValidator implements Validator<Document>, ValidatorListener {

  public static final Logger log = LogManager.getRootLogger();
  public static final Marker MARKER = MarkerManager.getMarker("SomeTestValidator");

	@Override
	public void validate(Context context, Document document) {
		// if (some test on document)
		// do something
		// else
		// context.report
		log.error(MARKER,
			"Some error is trigger validate in SomeTestValidator (custom DocumentValidation)"
		);
	}

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		log.error(MARKER,
			"Some error is trigger beforeMatching in SomeTestValidator (custom DocumentValidation)"
		);
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		document.getDocumentModel().addValidator(this);
		log.error(MARKER,
			"Some error is trigger beforeValidate in SomeTestValidator (custom DocumentValidation)"
		);
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		log.error(MARKER,
			"Some error is trigger afterValidate in SomeTestValidator (custom DocumentValidation)"
		);
		context.report(context.createError(DgprErrorCodes.DGPR_DOCUMENT_TEST_ERROR)
				.setMessageParam("DOCUMENT_NAME", document.getDocumentName())
		);
	}

}
