package fr.ign.validator.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.normalize.DocumentNormalizer;

/**
 * Invoke {@link DocumentNormalizer} to normalize input data in validation
 * directory.
 * 
 * @author MBorne
 *
 */
public class NormalizePostProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("NormalizePostProcess");

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
        if (!context.isNormalizeEnabled()) {
            log.info(MARKER, "Skipped as normalize is disabled (use --normalize)");
            return;
        }
        log.info(MARKER, "Normalize input data...");
        DocumentNormalizer normalizer = new DocumentNormalizer();
        normalizer.normalize(context, document);
        log.info(MARKER, "Normalize input data : completed.");
    }

}
