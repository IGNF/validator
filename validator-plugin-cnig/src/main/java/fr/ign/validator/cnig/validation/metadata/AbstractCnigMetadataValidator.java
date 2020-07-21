package fr.ign.validator.cnig.validation.metadata;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.validation.Validator;

/**
 * Base class for custom Metadata validation providing registration on each
 * Metadata FileModel.
 * 
 * @author mborne
 *
 */
public abstract class AbstractCnigMetadataValidator implements Validator<Metadata>, ValidatorListener {

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
        for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof MetadataModel)) {
                continue;
            }
            ((MetadataModel) fileModel).addMetadataValidator(this);
        }
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {

    }
}
