package fr.ign.validator.dgpr.validation.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.validation.Validator;

public class FilenamePrefixValidator implements Validator<TableFile>, ValidatorListener {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("FilenamePrefixValidator");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void validate(Context context, TableFile tableFile) {
		// String prefix = "TRI_TEST";
		// directory name follow this convention
		// [prefixTri]_SIG_DI
		if (!context.getCurrentDirectory().getName().contains("_SIG_DI")) {
			// l'erreur sera detecte au niveau du repertoire
			log.error(MARKER, String.format(
					"current directory name must contain _SIG_DI but found %1s", context.getCurrentDirectory().getName()
			));
			// fin de validation
			return;
		}
		String prefix = context.getCurrentDirectory().getName().split("_SIG_DI")[0];
		// filename follow this convention
		// N_[prefixTri]_TABLE_ddd.tab
		String filename = tableFile.getPath().getName();
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
