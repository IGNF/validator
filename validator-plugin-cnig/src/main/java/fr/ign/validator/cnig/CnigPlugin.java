package fr.ign.validator.cnig;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.process.CnigInfoExtractorPostProcess;
import fr.ign.validator.cnig.process.CreateShapefilesPostProcess;
import fr.ign.validator.cnig.process.ReferenceActeSupPostProcess;
import fr.ign.validator.cnig.validation.attribute.IdurbaValidator;
import fr.ign.validator.cnig.validation.attribute.InseeValidator;
import fr.ign.validator.cnig.validation.document.AtLeastOneWritingMaterialValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataDateOfLastRevisionValidator;
import fr.ign.validator.cnig.validation.metadata.CnigMetadataIdentifierValidator;
import fr.ign.validator.cnig.validation.metadata.CnigSpecificationsValidator;
import fr.ign.validator.cnig.validation.metadata.CnigTypeValidator;
import fr.ign.validator.plugin.Plugin;

public class CnigPlugin implements Plugin {

	@Override
	public String getName() {
		return "CNIG";
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
		context.addListener( new IdurbaValidator() );
		
		/*
		 * extends metadata validation
		 */
		context.addListener( new CnigSpecificationsValidator() );
		context.addListener( new CnigMetadataIdentifierValidator() );
		context.addListener( new CnigMetadataDateOfLastRevisionValidator() );
		context.addListener( new CnigTypeValidator() );
	}

}
