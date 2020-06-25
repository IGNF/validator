package fr.ign.validator.cnig.validation.metadata;

import org.apache.commons.lang.StringUtils;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.ReferenceSystemIdentifier;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.repository.ProjectionRepository;
import fr.ign.validator.validation.Validator;

/**
 * Ensures that ReferenceSystemIdentifier is defined according to guidelines :
 * 
 * http://cnig.gouv.fr/wp-content/uploads/2017/12/171219_Consignes_saisie_metadonnees_DU_v2017.pdf#page=10
 * 
 * @author MBorne
 *
 */
public class CnigMetadataReferenceSystemIdentifierValidator implements Validator<Metadata>, ValidatorListener {

    private ProjectionRepository projectionRepository = ProjectionRepository.getInstance();

    @Override
    public void validate(Context context, Metadata metadata) {
        ReferenceSystemIdentifier referenceSystemIdentifier = metadata.getReferenceSystemIdentifier();
        if (referenceSystemIdentifier == null) {
            context.report(
                CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_NOT_FOUND
            );
            return;
        }

        /* uri validation */
        String uri = referenceSystemIdentifier.getUri();
        if (StringUtils.isEmpty(uri)) {
            context.report(
                CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND
            );
            return;
        }
        Projection projection = projectionRepository.findByUri(uri);
        if (projection == null) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_UNEXPECTED)
                    .setMessageParam("URI", uri)
            );
            return;
        }

        /* code validation */
        String code = referenceSystemIdentifier.getCode();
        if (StringUtils.isEmpty(code)) {
            context.report(
                CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_NOT_FOUND
            );
            return;
        }

        if (!code.equals(projection.getCode())) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_CODE_INVALID)
                    .setMessageParam("CODE", code)
                    .setMessageParam("URI", projection.getUri())
                    .setMessageParam("EXPECTED_CODE", projection.getCode())
            );
            return;
        }
    }

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
