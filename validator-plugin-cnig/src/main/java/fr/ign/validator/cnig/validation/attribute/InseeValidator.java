package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.utils.InseeUtils;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.validation.Validator;

/**
 * Extends validator to validate INSEE codes
 * 
 * @author MBorne
 *
 */
public class InseeValidator implements Validator<Attribute<String>>, ValidatorListener {
	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("InseeValidator") ;

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		List<FileModel> fileModels = document.getDocumentModel().getFileModels() ;
		for (FileModel fileModel : fileModels) {
			if ( fileModel instanceof TableModel ){
				FeatureType featureType = fileModel.getFeatureType() ;
				AttributeType<?> attributeType = featureType.getAttribute("INSEE") ;
				if ( null != attributeType && attributeType instanceof StringType ){
					log.info(MARKER, "Ajout de InseeValidator à {}",attributeType.getName());
					((StringType)attributeType).addValidator(new InseeValidator());					
				}
			}
		}
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void validate(Context context, Attribute<String> validatable) {
		String insee = validatable.getBindedValue() ;
		if ( ! InseeUtils.isValidCommune(insee) ){
			context.report(CnigErrorCodes.CNIG_INSEE_INVALID, insee);			
		}
	}


}
