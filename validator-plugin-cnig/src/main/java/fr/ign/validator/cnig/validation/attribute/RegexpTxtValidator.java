package fr.ign.validator.cnig.validation.attribute;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentName;
import fr.ign.validator.cnig.model.DocumentType;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.validation.Validator;

public class RegexpTxtValidator implements Validator<Attribute<String>>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("RegexpTxtValidator");

    private static final String DOCUMENT_MODEL_NAME = "cnig_PLUi_2017";
    private static final String FILE_MODEL_NAME = "PRESCRIPTION_SURF";
    private static final String ATTRIBUTE_NAME = "TXT";

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        if (!document.getDocumentModel().getName().equalsIgnoreCase(DOCUMENT_MODEL_NAME)) {
            return;
        }
        List<FileModel> fileModels = document.getDocumentModel().getFileModels();
        for (FileModel fileModel : fileModels) {
            if (!(fileModel instanceof SingleTableModel)) {
                continue;
            }
            if (!fileModel.getName().equals(FILE_MODEL_NAME)) {
                continue;
            }
            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            AttributeType<?> attributeType = featureType.getAttribute(ATTRIBUTE_NAME);
            if (null != attributeType && attributeType instanceof StringType) {
                log.info(MARKER, "Ajout de RegexpTxtValidator à {}", attributeType.getName());
                ((StringType) attributeType).addValidator(new RegexpTxtValidator());
            }
        }
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void validate(Context context, Attribute<String> validatable) {
        String value = validatable.getBindedValue();
        if (value == null || !value.startsWith("SECT")) {
            return;
        }
        // TYPEPSC NOT LIKE '97' OR STYPEPSC NOT LIKE '01' OR TXT LIKE 'SECT__'
        if (!Pattern.matches("SECT[0-9]{2}", value)) {
            ValidatorError error = context.createError(CnigErrorCodes.CNIG_TXT_REGEXP_INVALID)
                .setMessageParam("VALUE", value);
            context.report(error);
        }
    }

}
