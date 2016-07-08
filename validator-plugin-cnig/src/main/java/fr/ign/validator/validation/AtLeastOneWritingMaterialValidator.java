package fr.ign.validator.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Document;
import fr.ign.validator.model.DocumentFile;
import fr.ign.validator.model.Validator;
import fr.ign.validator.model.file.PdfModel;

/**
 * 
 * Vérifie qu'il y a au moins une pièce écrite dans le document
 * 
 * @author MBorne
 *
 */
public class AtLeastOneWritingMaterialValidator implements Validator<Document>, ValidatorListener {

	public static final Logger log    = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("AtLeastOnePdfValidator") ;
	
	@Override
	public void validate(Context context, Document document) {
		int count = 0 ;
		for (DocumentFile documentFile : document.getDocumentFiles()) {
			if ( documentFile.getFileModel() instanceof PdfModel ){
				count++ ;
			}
		}
		log.info(MARKER,"{} pièces écrites trouvées",count);
		if ( count == 0){
			context.report(ErrorCode.CNIG_DOCUMENT_NO_PDF);
		}
	}

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
		
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		document.getDocumentModel().addValidator(this);
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		
	}

}
