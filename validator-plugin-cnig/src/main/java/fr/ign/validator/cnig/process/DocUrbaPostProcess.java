package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.tools.IdurbaFormat;
import fr.ign.validator.cnig.tools.IdurbaFormatFactory;
import fr.ign.validator.cnig.tools.TyperefExtractor;
import fr.ign.validator.data.Document;

/**
 * Post-process DOC_URBA table :
 * <ul>
 * <li>Filter rows in DATA/DOC_URBA.csv where IDURBA is not expected for the
 * given document</li>
 * <li>Locate line corresponding to the expected IDURBA</li>
 * </ul>
 */
public class DocUrbaPostProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocUrbaPostProcess");

    /**
     * Tag to store typeref found for document
     */
    private static final String TAG_TYPEREF = "typeref";

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        /*
         * TODO sd-redmine-6627 - filter rows according to expected IDURBA in
         * DATA/DOC_URBA.csv
         * 
         * So refactor TyperefExtractor to ZoneUrbaFilter...
         */
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

        IdurbaFormat idurbaFormat = IdurbaFormatFactory.getFormat(context.getDocumentModel());
        if (null == idurbaFormat) {
            log.info(MARKER, "TYPEREF ne sera pas extrait, le document n'est pas un DU");
            return null;
        }

        File docUrbaFile = new File(context.getDataDirectory(), "DOC_URBA.csv");
        if (!docUrbaFile.exists()) {
            log.error(MARKER, "Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
        }

        TyperefExtractor typerefExtractor = new TyperefExtractor(idurbaFormat);
        String result = typerefExtractor.findTyperef(docUrbaFile, documentName);
        if (null == result) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_IDURBA_NOT_FOUND)
                    .setMessageParam("EXPECTED_IDURBA", idurbaFormat.getRegexpHelp(documentName))
            );
        }
        return result;
    }

}
