package fr.ign.validator.cnig.process;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.tools.IdurbaFormat;
import fr.ign.validator.cnig.tools.IdurbaFormatFactory;
import fr.ign.validator.cnig.validation.attribute.IdurbaFormatValidator;
import fr.ign.validator.cnig.validation.attribute.IdurbaValidator;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;

/**
 * 
 * Customize validation to validate IDURBA attributes.
 * 
 * @author MBorne
 *
 */
public class CustomizeIdurbaPreProcess implements ValidatorListener {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CustomizeIdurbaPreProcess");

    /**
     * Extends the validation of DOC_URBA table
     * 
     * @param context
     * @param document
     * @throws Exception
     */
    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        /*
         * Retreive the format corresponding to the DocumentModel version
         */
        IdurbaFormat idurbaFormat = IdurbaFormatFactory.getFormat(context.getDocumentModel());
        if (idurbaFormat == null) {
            log.info(MARKER, "IDURBA validation is not supported for this type of document");
            return;
        }

        log.info(MARKER, "Customize IDURBA field validation...");

        /*
         * Customize IDURBA fields validation.
         */
        List<FileModel> fileModels = document.getDocumentModel().getFileModels();
        for (FileModel fileModel : fileModels) {
            if (fileModel instanceof TableModel) {
                FeatureType featureType = fileModel.getFeatureType();
                AttributeType<?> attribute = featureType.getAttribute("IDURBA");
                if (attribute == null) {
                    continue;
                }
                /* check attribute type and add custom validator */
                if (!(attribute instanceof StringType)) {
                    throw new RuntimeException(
                        String.format(
                            "%s.IDURBA should be of type 'String' (found '%s')",
                            featureType.getName(),
                            attribute.getTypeName()
                        )
                    );
                }
                StringType idurbaAttribute = (StringType) attribute;
                if (fileModel.getName().equalsIgnoreCase("DOC_URBA")) {
                    log.info(MARKER, "Add validator to ensure that IDURBA format is valid for {}", fileModel);
                    idurbaAttribute.addValidator(new IdurbaFormatValidator());
                } else {
                    log.info(MARKER, "Add validator to ensure that IDURBA matches document name for {}", fileModel);
                    idurbaAttribute.addValidator(
                        new IdurbaValidator(
                            idurbaFormat,
                            document.getDocumentName()
                        )
                    );
                }
            }
        }

        log.info(MARKER, "Customize IDURBA field validation : completed.");
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

}
