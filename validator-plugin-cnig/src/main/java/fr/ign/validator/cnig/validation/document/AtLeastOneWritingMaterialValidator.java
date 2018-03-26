package fr.ign.validator.cnig.validation.document;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Checking the document contains at least one written material ("Pièce écrite")
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
			context.report(CnigErrorCodes.CNIG_DOCUMENT_NO_PDF);
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
