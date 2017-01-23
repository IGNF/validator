package fr.ign.validator.cnig;

import fr.ign.validator.Context;
import fr.ign.validator.Plugin;
import fr.ign.validator.cnig.process.CnigInfoExtractorPostProcess;
import fr.ign.validator.cnig.process.CreateShapefilesPostProcess;
import fr.ign.validator.cnig.process.ReferenceActeSupPostProcess;
import fr.ign.validator.cnig.validation.AtLeastOneWritingMaterialValidator;
import fr.ign.validator.cnig.validation.IdurbaValidator;
import fr.ign.validator.cnig.validation.InseeValidator;

public class CnigPlugin implements Plugin {

	@Override
	public String getName() {
		return "CNIG";
	}

	@Override
	public void setup( Context context ) {
		context.addListener( new ReferenceActeSupPostProcess() );
		context.addListener( new CreateShapefilesPostProcess() );
		context.addListener( new CnigInfoExtractorPostProcess() );
		
		/*
		 * ajout des validateurs personnalisés
		 */
		context.addListener( new InseeValidator() );
		context.addListener( new AtLeastOneWritingMaterialValidator() );
		context.addListener( new IdurbaValidator() );
	}
	
}
