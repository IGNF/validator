package fr.ign.validator.cnig;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.process.CreateShapefilesPostProcess;
import fr.ign.validator.cnig.process.CustomizeIdurbaPreProcess;
import fr.ign.validator.cnig.process.DocUrbaComPostProcess;
import fr.ign.validator.cnig.process.DocUrbaPostProcess;
import fr.ign.validator.cnig.process.PerimetreScotPostProcess;
import fr.ign.validator.cnig.process.SupRelationsPostProcess;
import fr.ign.validator.cnig.validation.attribute.GeometryComplexityValidator;
import fr.ign.validator.cnig.validation.attribute.GeometryInsideDocumentValidator;
import fr.ign.validator.cnig.validation.attribute.InseeValidator;
import fr.ign.validator.cnig.validation.attribute.RegexpTxtValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneAssietteValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneGenerateurValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneWritingMaterialValidator;
import fr.ign.validator.cnig.validation.document.DocumentNameValidator;
import fr.ign.validator.cnig.validation.document.FileExtensionValidator;
import fr.ign.validator.cnig.validation.document.PieceEcriteOnlyPdfValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataDateOfLastRevisionValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataKeywordsValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataReferenceSystemIdentifierValidator;
import fr.ign.validator.cnig.validation.metadata.CnigSpecificationsValidator;
import fr.ign.validator.cnig.validation.metadata.CnigTypeValidator;
import fr.ign.validator.plugin.Plugin;
import fr.ign.validator.process.DocumentInfoExtractorPostProcess;

/**
 * Customizes validator for CNIG standard validation
 * 
 * @see <a href=
 *      "http://www.geoportail-urbanisme.gouv.fr/standard">http://www.geoportail-urbanisme.gouv.fr/standard</a>
 * 
 * @author MBorne
 *
 */
public class CnigPlugin implements Plugin {

    public static final String NAME = "CNIG";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void setup(Context context) {
        // --normalize is required with CNIG plugin
        context.setNormalizeEnabled(true);

        /*
         * PreProcess - Customize idurba
         */
        context.addListener(new CustomizeIdurbaPreProcess());

        /*
         * PostProcess - Join SUP files to add a column "fichiers" and "nomsuplitt"
         * (must run before CreateShapefilesPostProcess)
         */
        context.addListener(new SupRelationsPostProcess());
        /*
         * PostProcess- Converts DATA/*.csv to DATA/*.shp (must follow
         * ReferenceActeSupPostProcess)
         */
        context.addListener(new CreateShapefilesPostProcess());
        /*
         * PostProcess - Compute document.tag.typeref (must run before
         * document-info.json generation)
         */
        context.addListenerBefore(
            new DocUrbaPostProcess(),
            DocumentInfoExtractorPostProcess.class
        );

        /*
         * PostProcess - DOC_URBA_COM validation for PLUi
         */
        context.addListener(new DocUrbaComPostProcess());

        /*
         * PostProcess - Extends PERIMETRE_SCOT validation
         */
        context.addListener(new PerimetreScotPostProcess());

        /*
         * Extends document validation
         */
        context.addListener(new DocumentNameValidator());
        context.addListener(new AtLeastOneWritingMaterialValidator());
        context.addListener(new AtLeastOneGenerateurValidator());
        context.addListener(new AtLeastOneAssietteValidator());
        if (context.isFlatValidation()) {
            context.addListener(new FileExtensionValidator());
        } else {
            context.addListener(new PieceEcriteOnlyPdfValidator());
        }

        /*
         * Extends attribute validation
         */
        context.addListener(new InseeValidator());
        context.addListener(new RegexpTxtValidator());
        context.addListener(new GeometryComplexityValidator());
        context.addListener(new GeometryInsideDocumentValidator());

        /*
         * Extends metadata validation
         */
        context.addListener(new CnigTypeValidator());
        context.addListener(new CnigSpecificationsValidator());
        context.addListener(new CnigMetadataDateOfLastRevisionValidator());
        context.addListener(new CnigMetadataReferenceSystemIdentifierValidator());
        context.addListener(new CnigMetadataKeywordsValidator());
    }

}
