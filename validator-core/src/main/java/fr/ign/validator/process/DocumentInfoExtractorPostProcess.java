package fr.ign.validator.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.info.DocumentInfoExtractor;
import fr.ign.validator.info.DocumentInfoWriter;
import fr.ign.validator.info.model.DocumentInfo;

/**
 *
 * Produce a document-info.json file with various informations about the
 * validated document
 *
 * Note that this feature extends and replaces the previous "cnig-infos.xml"
 * specific to validator-cnig-plugin
 *
 * @see DocumentInfo
 *
 * @author CBouche
 *
 */
public class DocumentInfoExtractorPostProcess implements ValidatorListener {
    private static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("DocumentInfoExtractorPostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        if (!context.isNormalizeEnabled()) {
            log.warn(MARKER, "skip document-info.json generation (--normalize is required)");
            return;
        }

        log.info(MARKER, "Retrieve informations to build document-info.json...");
        DocumentInfoExtractor infoExtractor = new DocumentInfoExtractor();
        DocumentInfo documentInfo = infoExtractor.parseDocument(context, document);

        File documentInfoPath = new File(context.getValidationDirectory(), "document-info.json");
        log.info(MARKER, "Save {}...", documentInfoPath);
        DocumentInfoWriter infowriter = new DocumentInfoWriter();
        infowriter.write(documentInfo, documentInfoPath);
        log.info(MARKER, "Retrieve informations to build document-info.json : completed.");
    }

}
