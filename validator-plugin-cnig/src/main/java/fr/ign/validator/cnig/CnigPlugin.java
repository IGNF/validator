package fr.ign.validator.cnig;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.process.CnigInfoExtractorPostProcess;
import fr.ign.validator.cnig.process.CreateShapefilesPostProcess;
import fr.ign.validator.cnig.process.ReferenceActeSupPostProcess;
import fr.ign.validator.cnig.validation.attribute.IdurbaValidationCustomizer;
import fr.ign.validator.cnig.validation.attribute.InseeValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneWritingMaterialValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataDateOfLastRevisionValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataIdentifierValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataReferenceSystemIdentifierValidator;
import fr.ign.validator.cnig.validation.metadata.CnigSpecificationsValidator;
import fr.ign.validator.cnig.validation.metadata.CnigTypeValidator;
import fr.ign.validator.plugin.Plugin;

/**
 * Customizes validator for CNIG standard validation
 * 
 * @see <a href="http://www.geoportail-urbanisme.gouv.fr/standard">http://www.geoportail-urbanisme.gouv.fr/standard</a>
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
	public void setup( Context context ) {
		// Join SUP files to add a column "fichiers" 
		context.addListener( new ReferenceActeSupPostProcess() );
		// converts DATA/*.csv to DATA/*.shp
		context.addListener( new CreateShapefilesPostProcess() );
		// produce cnig-infos.xml
		context.addListener( new CnigInfoExtractorPostProcess() );
		
		/*
		 * extends attribute validation
		 */
		context.addListener( new InseeValidator() );
		context.addListener( new AtLeastOneWritingMaterialValidator() );
		context.addListener( new IdurbaValidationCustomizer() );

		/*
		 * extends metadata validation
		 */
		context.addListener( new CnigTypeValidator() );
		context.addListener( new CnigSpecificationsValidator() );
		context.addListener( new CnigMetadataIdentifierValidator() );
		context.addListener( new CnigMetadataDateOfLastRevisionValidator() );
		context.addListener( new CnigMetadataReferenceSystemIdentifierValidator() );
	}

}
