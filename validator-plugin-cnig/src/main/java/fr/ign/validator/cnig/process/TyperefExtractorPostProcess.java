package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.cnig.idurba.IdurbaHelperFactory;
import fr.ign.validator.cnig.utils.TyperefExtractor;
import fr.ign.validator.data.Document;

/**
 * Extract typeref (cadastral reference)
 */
public class TyperefExtractorPostProcess implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TyperefExtractorPostProcess");

	private static final String TAG_TYPEREF = "typeref";

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		document.setTag(TAG_TYPEREF, parseTyperef(context, document));
	}

	/**
	 * Get typeref value from DOC_URBA.csv file
	 * 
	 * @param context
	 * @return null if not found
	 */
	private String parseTyperef(Context context, Document document) {
		String documentName = document.getDocumentName();

		IdurbaHelper helper = IdurbaHelperFactory.getInstance(context.getDocumentModel());
		if (null == helper) {
			log.info(MARKER, "TYPEREF ne sera pas extrait, le document n'est pas un DU");
			return null;
		}

		File documentDirectory = new File(context.getValidationDirectory(), documentName);
		File dataDirectory = new File(documentDirectory, "DATA");
		File docUrbaFile = new File(dataDirectory, "DOC_URBA.csv");

		if (!docUrbaFile.exists()) {
			log.error(MARKER, "Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
		}

		TyperefExtractor typerefExtractor = new TyperefExtractor(helper);
		String result = typerefExtractor.findTyperef(docUrbaFile, documentName);
		if (null == result) {
			context.report(
				CnigErrorCodes.CNIG_IDURBA_NOT_FOUND, 
				helper.getHelpExpected(documentName)
			);
		}
		return result;
	}

	
}
