package fr.ign.validator.cnig.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.utils.ReferenceActeSupJointureBuilder;
import fr.ign.validator.data.Document;


/**
 * 
 * Adds a field `Files` to "génératuers" and "assiettes" to join with "actes" and "servitudes"
 * 
 * @author MBorne
 */
public class ReferenceActeSupPostProcess implements ValidatorListener {
	
	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker POSTPROCESS_REFERENCEACTE = MarkerManager.getMarker("POSTPROCESS_REFERENCEACTE") ;


	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}


	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		ReferenceActeSupJointureBuilder jointureBuilder = new ReferenceActeSupJointureBuilder(context);
		jointureBuilder.run() ;
	}
	

}
