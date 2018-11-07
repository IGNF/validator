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

public class DocumentPrefixValidator implements Validator<Document>, ValidatorListener {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentPrefixValidator");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void validate(Context context, Document document) {
		// String prefix = "TRI_TEST";
		// directory name follow this convention
		// [prefixTri]_SIG_DI
		if (!document.getDocumentName().contains("_SIG_DI")) {
			context.report(context.createError(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR)
				.setMessageParam("DOCUMENT_NAME", document.getDocumentName())
			);
		}
	}

}
