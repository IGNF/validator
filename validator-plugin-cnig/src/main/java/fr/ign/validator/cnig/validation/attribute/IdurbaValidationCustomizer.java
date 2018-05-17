package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.cnig.idurba.IdurbaHelperFactory;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;

/**
 * 
 * Customize IdurbaValidation (must be separated from IdurbaValidator cause it depends on document type)
 * 
 * @author MBorne
 *
 */
public class IdurbaValidationCustomizer implements ValidatorListener {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("IdurbaValidationCustomizer");

	/**
	 * Extends the validation of DOC_URBA table
	 * 
	 * @param context
	 * @param document
	 * @throws Exception
	 */
	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		/*
		 * Configure idurbaHelper according to document model
		 */
		IdurbaHelper idurbaHelper = IdurbaHelperFactory.getInstance(context.getDocumentModel());
		if ( idurbaHelper == null ){
			log.info(MARKER, "IDURBA validation not supported for this type of document");
			return;
		}
		/*
		 * Adding this validator to DOC_URBA table
		 */
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			if ( fileModel instanceof TableModel ){
				AttributeType<?> attribute = fileModel.getFeatureType().getAttribute("IDURBA") ;
				if ( attribute == null ){
					continue;
				}
				/* check attribute type and add custom validator */
				if ( attribute instanceof StringType ){
					((StringType)attribute).addValidator(new IdurbaValidator(idurbaHelper));
				}else{
					throw new RuntimeException("IDURBA de DOC_URBA n'est pas configuré comme étant une chaîne de caractère");
				}
			}
		}
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}
	
}
