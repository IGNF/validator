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
import fr.ign.validator.cnig.validation.database.IdassIsUniqueValidator;
import fr.ign.validator.cnig.validation.database.IdgenExistsValidator;
import fr.ign.validator.cnig.validation.database.IdgenIsUniqueValidator;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
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
            log.info(MARKER, "Skipped - document is not a SUP");
            return;
        }

        log.info(MARKER, "Disable unique constraint validation for IDGEN and IDASS");
        for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof SingleTableModel)) {
                continue;
            }

            FeatureType featureType = ((TableModel) fileModel).getFeatureType();
            if (featureType == null) {
                continue;
            }
            for (String idName : PATCHED_ATTRIBUTES) {
                AttributeType<?> attribute = featureType.getAttribute(idName);
                if (attribute != null && attribute.getConstraints().isUnique()) {
                    log.info(MARKER, "Disable unique constraint for {}", attribute);
                    attribute.getConstraints().setUnique(false);
                }
            }
        }
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        if (!isSupDocument(context.getDocumentModel())) {
            log.info(MARKER, "Skipped - document is not a SUP");
            return;
        }

        log.info(MARKER, "Post-process GENERATEUR and ASSIETTE tables...");

        log.info(MARKER, "Merge GENERATEUR and ASSIETTE tables in validation database to explore relations...");
        DatabaseSUP database = DatabaseSUP.createFromValidationDatabase(context);
        if (database == null) {
            log.error(MARKER, "Skipped - fail to create DatabaseSUP.");
            return;
        }

        log.info(
            MARKER,
            "Perform joins to add columns 'fichier', 'nomsuplitt' and 'nomreg' to normalized GENERATEUR and ASSIETTE tables..."
        );
        File tempDirectory = getTempDirectory(context);
        AdditionalColumnsBuilder builder = new AdditionalColumnsBuilder(
            database,
            tempDirectory
        );
        builder.addColumnsToGenerateurAndAssietteFiles(context.getDataDirectory());

        log.info(MARKER, "Validate IDGEN and IDASS on merged GENERATEUR and ASSIETTE tables...");
        for (Validator<DatabaseSUP> validator : validators) {
            validator.validate(context, database);
        }

        log.info(MARKER, "Post-process GENERATEUR and ASSIETTE tables : completed.");
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
        File tempDirectory = new File(context.getDataDirectory(), "tmp");
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs();
        }
        return tempDirectory;
    }

}
