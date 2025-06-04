package fr.ign.validator.validation.file.metadata;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.ResponsibleParty;
import fr.ign.validator.validation.Validator;

/**
 *
 * Ensures that "metadataContact" is defined
 *
 * @author MBorne
 *
 */
public class MetadataContactValidator implements Validator<Metadata> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("MetadataContactValidator");

    @Override
    public void validate(Context context, Metadata metadata) {
        ResponsibleParty contact = metadata.getMetadataContact();
        if (null == contact) {
            context.report(
                CoreErrorCodes.METADATA_METADATACONTACT_NOT_FOUND
            );
            return;
        }
        if (!contact.mailIsValid()) {
            context.report(
                context.createError(CoreErrorCodes.METADATA_METADATACONTACT_MAILINVALID)
                    .setMessageParam("VALUE", contact.getElectronicMailAddress())
            );
        }
        if (!contact.hasName()) {
            context.report(
                context.createError(CoreErrorCodes.METADATA_METADATACONTACT_NAMEABSENT)
            );
        }
    }

}
