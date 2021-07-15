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
import fr.ign.validator.cnig.tools.DocUrbaFilter;
import fr.ign.validator.data.Document;

/**
 * Post-process DOC_URBA table :
 * <ul>
 * <li>Filter rows according to the expected IDURBA for the document</li>
 * <li>Extract corresponding typeref (document.tags.typeref)</li>
 * </ul>
 * </ul>
 */
public class DocUrbaPostProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocUrbaPostProcess");
    /**
     * Tag to store IDURBA for the document
     */
    private static final String TAG_IDURBA = "idurba";
    /**
     * Tag to store TYPEREF for the document
     */
    private static final String TAG_TYPEREF = "typeref";

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        IdurbaFormat idurbaFormat = IdurbaFormatFactory.getFormat(context.getDocumentModel());
        if (null == idurbaFormat) {
            log.info(MARKER, "Skipped, document is not a DU");
            return;
        }

        log.info(
            MARKER,
            "Filter DOC_URBA table and retrieve metadata..."
        );

        String documentName = document.getDocumentName();
        DocUrbaFilter docUrbaFilter = new DocUrbaFilter(idurbaFormat, documentName);
        File docUrbaFile = new File(context.getDataDirectory(), "DOC_URBA.csv");
        DocUrbaFilter.Result result = docUrbaFilter.process(docUrbaFile);

        /*
         * check number of rows in filtered DOC_URBA
         */
        log.info(MARKER, "{} row(s) found in filtered DOC_URBA.", result.count);
        if (result.count == 0) {
            log.info(MARKER, "Report CNIG_IDURBA_NOT_FOUND as DOC_URBA is empty.");
            context.report(
                context.createError(CnigErrorCodes.CNIG_IDURBA_NOT_FOUND)
                    .setMessageParam("EXPECTED_IDURBA", idurbaFormat.getRegexpHelp(documentName))
            );
        } else if (result.count > 1) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_IDURBA_MULTIPLE_FOUND)
                    .setMessageParam("EXPECTED_IDURBA", idurbaFormat.getRegexpHelp(documentName))
            );
        }

        /*
         * Add idurba and typeref to document tags.
         */
        log.info(MARKER, "Found idurba={} in filtered DOC_URBA.", result.idurba);
        document.setTag(TAG_IDURBA, result.idurba);
        log.info(MARKER, "Found typeref={} in filtered DOC_URBA.", result.typeref);
        document.setTag(TAG_TYPEREF, result.typeref);

        log.info(
            MARKER,
            "Filter DOC_URBA table and retrieve metadata : completed"
        );
    }

}
