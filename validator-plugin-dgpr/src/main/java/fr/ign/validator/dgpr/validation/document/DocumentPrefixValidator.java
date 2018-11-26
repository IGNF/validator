package fr.ign.validator.dgpr.validation.document;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class DocumentPrefixValidator implements Validator<Document>, ValidatorListener {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentPrefixValidator");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		document.getDocumentModel().addValidator(this);
		log.info(MARKER,
			"add DocumentPrefixValidator to DocumentModel"
		);
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void validate(Context context, Document document) {
		// PART 1, document name prefix
		// String prefix = "TRI_TEST";
		// directory name follow this convention
		// [prefixTri]_SIG_DI
		if (!document.getDocumentName().contains("_SIG_DI")) {
			context.report(context.createError(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR)
					.setMessageParam("DOCUMENT_NAME", document.getDocumentName())
			);
			// filename prefix validation is ignored in that case
			return;
		}

		// PART 2, filename prefix
		// filename follow this convention
		// N_[prefixTri]_TABLE_ddd.tab
		List<DocumentFile> files = document.getDocumentFiles();
		for (DocumentFile documentFile : files) {
			validateFilenamePrefix(context, documentFile);
		}
	}

	public void validateFilenamePrefix(Context context, DocumentFile documentFile) {
		String prefix = context.getCurrentDirectory().getName().split("_SIG_DI")[0];
		String filename = documentFile.getPath().getName();
		if (filename.contains(prefix)) {
			// all is ok
		} else {
			context.report(context.createError(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR)
					.setMessageParam("FILENAME", filename)
					.setMessageParam("DOCUMENT_NAME", prefix)
			);
		}
	}

}
