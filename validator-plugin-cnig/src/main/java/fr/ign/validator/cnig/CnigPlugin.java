package fr.ign.validator.cnig;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.process.CreateShapefilesPostProcess;
import fr.ign.validator.cnig.process.CustomizeIdurbaPreProcess;
import fr.ign.validator.cnig.process.ReferenceActeSupPostProcess;
import fr.ign.validator.cnig.process.DocUrbaPostProcess;
import fr.ign.validator.cnig.validation.attribute.InseeValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneWritingMaterialValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataKeywordsValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataDateOfLastRevisionValidator;
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

        // Join SUP files to add a column "fichiers" (must be done before
        // CreateShapefilesPostProcess)
        context.addListener(new ReferenceActeSupPostProcess());
        // converts DATA/*.csv to DATA/*.shp (must follow ReferenceActeSupPostProcess)
        context.addListener(new CreateShapefilesPostProcess());
        /*
         * compute document.tag.typeref (must be done before document-info.json
         * generation)
         */
        context.addListenerBefore(
            new DocUrbaPostProcess(),
            DocumentInfoExtractorPostProcess.class
        );

        /*
         * extends attribute validation
         */
        context.addListener(new InseeValidator());
        context.addListener(new AtLeastOneWritingMaterialValidator());
        context.addListener(new CustomizeIdurbaPreProcess());

        /*
         * extends metadata validation
         */
        context.addListener(new CnigTypeValidator());
        context.addListener(new CnigSpecificationsValidator());
        context.addListener(new CnigMetadataDateOfLastRevisionValidator());
        context.addListener(new CnigMetadataReferenceSystemIdentifierValidator());
        context.addListener(new CnigMetadataKeywordsValidator());
    }

}
