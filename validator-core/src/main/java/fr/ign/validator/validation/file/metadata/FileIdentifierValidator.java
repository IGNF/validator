package fr.ign.validator.validation.file.metadata;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.validation.Validator;

/**
 *
 * Ensures that "fileIdentifier" is defined and not empty
 *
 * @author MBorne
 *
 */
public class FileIdentifierValidator implements Validator<Metadata> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("FileIdentifierValidator");

    /**
     * Allows [a-z][A-Z][0-9], spaces, "-", ".", ":" "/" "\"
     */
    public static final String REGEXP = "[\\w\\s-.:/\\[\\]]+";

    public boolean isValid(String fileIdentifier) {
        if (StringUtils.isEmpty(fileIdentifier)) {
            return false;
        }
        return fileIdentifier.matches(REGEXP);
    }

    @Override
    public void validate(Context context, Metadata metadata) {
        String fileIdentifier = metadata.getFileIdentifier();
        log.info(MARKER, "metadata.fileIdentifier : {}", fileIdentifier);
        if (StringUtils.isEmpty(fileIdentifier)) {
            context.report(
                CoreErrorCodes.METADATA_FILEIDENTIFIER_NOT_FOUND
            );
            return;
        }
        if (!isValid(fileIdentifier)) {
            context.report(
                context.createError(CoreErrorCodes.METADATA_FILEIDENTIFIER_INVALID)
                    .setMessageParam("VALUE", fileIdentifier)
                    .setMessageParam("EXPECTED_REGEXP", REGEXP)
            );
        }
    }

}
