package fr.ign.validator.validation.file.metadata;

import java.util.List;

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
 * Ensures that "identifier" is defined and not empty
 *
 * @author MBorne
 *
 */
public class IdentifiersValidator implements Validator<Metadata> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("IdentifierValidator");

    @Override
    public void validate(Context context, Metadata metadata) {
        /* a non empty identifier should be found */
        List<String> identifiers = metadata.getIdentifiers();
        for (String identifier : identifiers) {
            if (StringUtils.isEmpty(identifier)) {
                log.info(MARKER, "metadata.identifier : ignore empty identifier ({})", identifier);
                continue;
            }
            log.info(MARKER, "metadata.identifier : found ({})", identifier);
            return;
        }
        context.report(
            CoreErrorCodes.METADATA_IDENTIFIER_NOT_FOUND
        );
    }

}
