package fr.ign.validator.cnig.validation.metadata;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.model.DocumentName;
import fr.ign.validator.cnig.model.DocumentType;
import fr.ign.validator.cnig.validation.metadata.internal.RequiredKeyword;
import fr.ign.validator.data.Document;
import fr.ign.validator.metadata.Keywords;
import fr.ign.validator.metadata.Metadata;

/**
 * Validate metadata according to CNIG standards :
 * 
 * @see http://cnig.gouv.fr/wp-content/uploads/2020/05/200511_Consignes_saisie_metadonnees_DU_v2020-05.pdf#page=8
 *      for PLU,PLUI,POS,CC,PSMV documents
 * @see http://cnig.gouv.fr/wp-content/uploads/2020/05/200511_Consignes_saisie_metadonnees_SUP_v2020-05.pdf#page=8
 *      for SUP documents
 * @see http://cnig.gouv.fr/wp-content/uploads/2020/05/200511_Consignes_saisie_metadonnees_SCOT_v2020-05.pdf#page=7&zoom=auto,-92,444
 *      for SCOT documents
 * 
 * @author mborne
 *
 */
public class CnigMetadataKeywordsValidator extends AbstractCnigMetadataValidator {
    private static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("CnigMetadataKeywordsValidator");

    static final String THESAURUS_TYPE_DOC = "Types de document d’urbanisme";
    static final String THESAURUS_CATEGORIE_SUP = "nomenclature nationale des SUP";
    /**
     * Warning : used both for "manager" (SUP,SCOT) and "territory" (PLUi)
     */
    static final String THESAURUS_SIREN = "Répertoire SIRENE";

    static final String THESAURUS_COG = "Code officiel géographique au 1er janvier 20[0-9]{2}";

    static final String THESAURUS_CODE_DU = "Sous-code de documents d’urbanisme";

    /**
     * Provide the list of required keywords according to document name.
     * 
     * @param documentName
     * @return
     */
    public static List<RequiredKeyword> getRequiredKeywords(DocumentName documentName) {
        assert (documentName.isValid());
        List<RequiredKeyword> result = new ArrayList<>();
        /*
         * TYPE_DOC for non SUP documents
         */
        DocumentType documentType = documentName.getDocumentType();
        if (DocumentType.SUP == documentType) {
            result.add(
                new RequiredKeyword(
                    "CATEGORIE_SUP",
                    THESAURUS_CATEGORIE_SUP,
                    documentName.getCategory()
                )
            );
        } else {
            result.add(
                new RequiredKeyword(
                    "TYPE_DOC",
                    THESAURUS_TYPE_DOC,
                    documentType.toString().toUpperCase()
                )
            );
        }

        /*
         * GESTIONNAIRE for SUP and SCoT
         */
        if (DocumentType.SCoT == documentType || DocumentType.SUP == documentType) {
            result.add(
                new RequiredKeyword(
                    "SIREN_GESTIONNAIRE",
                    THESAURUS_SIREN,
                    documentName.getManager()
                )
            );
        }

        /**
         * EMPRISE except for SCoT
         */
        if (DocumentType.SCoT != documentType) {
            if (DocumentType.PLUi == documentType) {
                result.add(
                    new RequiredKeyword(
                        "EMPRISE",
                        THESAURUS_SIREN,
                        documentName.getTerritory()
                    )
                );
            } else {
                result.add(
                    new RequiredKeyword(
                        "EMPRISE",
                        THESAURUS_COG,
                        documentName.getTerritory()
                    )
                );
            }
        }

        if (!StringUtils.isEmpty(documentName.getPart())) {
            result.add(
                new RequiredKeyword(
                    "CodeDU",
                    THESAURUS_CODE_DU,
                    documentName.getPart()
                )
            );
        }

        return result;
    }

    @Override
    public void validate(Context context, Metadata metadata) {
        /*
         * retrieve document name
         */
        Document document = context.getDataByType(Document.class);
        assert (document != null);
        DocumentName documentName = new DocumentName(document.getDocumentName());
        if (!documentName.isValid()) {
            log.error(
                MARKER, "skip keywords validation as document name ('{}') is invalid", document.getDocumentName()
            );
            return;
        }

        log.info(MARKER, "Retrieve required keywords for {}...", documentName);
        List<RequiredKeyword> requiredKeywords = getRequiredKeywords(documentName);
        for (RequiredKeyword requiredKeyword : requiredKeywords) {
            log.info(
                MARKER, "Search keyword {} with thesaurusName : {}...", requiredKeyword.name,
                requiredKeyword.thesaurusName
            );
            Keywords keywords = findKeywordsByThesaurusName(metadata, requiredKeyword.thesaurusName);
            if (keywords == null) {
                log.error(
                    MARKER, "keyword {} with thesaurusName : {} not found!", requiredKeyword.name,
                    requiredKeyword.thesaurusName
                );
                context.report(
                    context.createError(CnigErrorCodes.CNIG_METADATA_KEYWORD_NOT_FOUND)
                        .setMessageParam("NAME", requiredKeyword.name)
                        .setMessageParam("THESAURUS_NAME", requiredKeyword.thesaurusName)
                );
                continue;
            }

            String value = getFirstValue(keywords);
            if (!value.equalsIgnoreCase(requiredKeyword.expectedValue)) {
                log.error(
                    MARKER, "Invalid value for keyword {} : {} (expected : {})",
                    requiredKeyword.name,
                    value,
                    requiredKeyword.expectedValue
                );
                context.report(
                    context.createError(CnigErrorCodes.CNIG_METADATA_KEYWORD_INVALID)
                        .setMessageParam("NAME", requiredKeyword.name)
                        .setMessageParam("THESAURUS_NAME", requiredKeyword.thesaurusName)
                        .setMessageParam("VALUE", value)
                        .setMessageParam("EXPECTED_VALUE", requiredKeyword.expectedValue)
                );
            }
        }
    }

    /**
     * Get first value in keyword list.
     * 
     * @param keywords
     * @return
     */
    private String getFirstValue(Keywords keywords) {
        if (keywords.getKeywords().isEmpty()) {
            return null;
        }
        return keywords.getKeywords().get(0);
    }

    /**
     * Find keyword by thesaurus name.
     * 
     * @param metadata
     * @param expectedThesaurusName
     * @return
     */
    private Keywords findKeywordsByThesaurusName(Metadata metadata, String expectedThesaurusName) {
        for (Keywords keyword : metadata.getKeywords()) {
            String thesaurusName = keyword.getThesaurusName();
            if (StringUtils.isEmpty(thesaurusName)) {
                continue;
            }
            if (thesaurusName.matches("(?i)" + expectedThesaurusName)) {
                return keyword;
            }
        }
        return null;
    }
}
