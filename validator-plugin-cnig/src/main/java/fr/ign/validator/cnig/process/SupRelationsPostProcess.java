package fr.ign.validator.cnig.process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.model.DocumentModelName;
import fr.ign.validator.cnig.model.DocumentType;
import fr.ign.validator.cnig.sup.AdditionalColumnsBuilder;
import fr.ign.validator.cnig.sup.DatabaseSUP;
import fr.ign.validator.cnig.sup.DatabaseSUPFactory;
import fr.ign.validator.cnig.validation.database.IdassIsUniqueValidator;
import fr.ign.validator.cnig.validation.database.IdgenExistsValidator;
import fr.ign.validator.cnig.validation.database.IdgenIsUniqueValidator;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.validation.Validator;

/**
 * Post-process relations between SUP tables to adds columns to "GENERATEUR" and
 * "ASSIETE" tables in output data.
 * 
 * @author MBorne
 */
public class SupRelationsPostProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("SupRelationsPostProcess");

    private List<Validator<DatabaseSUP>> validators = new ArrayList<>();

    private static final String[] PATCHED_ATTRIBUTES = new String[] {
        "IDASS", "IDGEN"
    };

    public SupRelationsPostProcess() {
        validators.add(new IdgenIsUniqueValidator());
        validators.add(new IdassIsUniqueValidator());
        validators.add(new IdgenExistsValidator());
    }

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        if (!isSupDocument(context.getDocumentModel())) {
            log.info(MARKER, "beforeMatching skipped (document is not a SUP)");
            return;
        }
        /*
         * disable default validation for unique constraint IDGEN and IDASS
         */
        for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
            FeatureType featureType = fileModel.getFeatureType();
            if (featureType == null) {
                continue;
            }
            for (String idName : PATCHED_ATTRIBUTES) {
                AttributeType<?> attribute = featureType.getAttribute(idName);
                if (attribute != null) {
                    attribute.getConstraints().setUnique(false);
                }
            }
        }
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        if (!isSupDocument(context.getDocumentModel())) {
            log.info(MARKER, "afterValidate skipped (document is not a SUP)");
            return;
        }

        /*
         * Create DatabaseSUP instance to explore relations.
         */
        File tempDirectory = getTempDirectory(context);
        DatabaseSUPFactory databaseFactory = new DatabaseSUPFactory(tempDirectory);
        DatabaseSUP database = databaseFactory.createFromDataDirectory(context.getDataDirectory());
        if (database == null) {
            log.warn(MARKER, "skipped due to failure in DatabaseSUP creation");
            return;
        }

        /*
         * Add columns to GENERATEUR and ASSIETTE in DATA directory
         * ('fichier','nomsuplitt',...)
         */
        AdditionalColumnsBuilder builder = new AdditionalColumnsBuilder(
            database,
            tempDirectory
        );
        builder.addColumnsToGenerateurAndAssietteFiles(context.getDataDirectory());

        /*
         * Apply custom validators on DatabaseSUP
         */
        for (Validator<DatabaseSUP> validator : validators) {
            validator.validate(context, database);
        }
    }

    /**
     * Test if document model is a SUP.
     * 
     * @param documentModel
     * @return
     */
    private boolean isSupDocument(DocumentModel documentModel) {
        String documentType = DocumentModelName.getDocumentType(documentModel.getName());
        return documentType.equalsIgnoreCase(DocumentType.SUP.toString());
    }

    /**
     * Get temp directory
     */
    private File getTempDirectory(Context context) {
        return new File(context.getDataDirectory(), "tmp");
    }

}
