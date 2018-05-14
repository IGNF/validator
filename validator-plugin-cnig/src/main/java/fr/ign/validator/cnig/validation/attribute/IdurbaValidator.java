package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.utils.IdurbaHelper;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Customize attributes named IDURBA validation
 * 
 * TODO
 * <ul>
 * 	<li>Define IdurbaHelper as a constructor parameter</li>
 *  <li>Move ValidatorListener implementation to process.CustomizeDocumentModel (do the same for other validators)</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class IdurbaValidator implements Validator<Attribute<String>>, ValidatorListener {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("IDURBA_VALIDATOR");

	/**
	 * idurbaHelper configured according to document model (see beforeMatching)
	 */
	private IdurbaHelper idurbaHelper;
	
	public IdurbaValidator(){

	}
	
	
	public IdurbaHelper getIdurbaHelper() {
		return idurbaHelper;
	}

	protected void setIdurbaHelper(IdurbaHelper idurbaHelper) {
		this.idurbaHelper = idurbaHelper;
	}



	@Override
	public void validate(Context context, Attribute<String> attribute) {
		if ( ! idurbaHelper.isValid(attribute.getBindedValue()) ){
			context.report(
				CnigErrorCodes.CNIG_IDURBA_INVALID, 
				attribute.getBindedValue(),
				idurbaHelper.getHelpFormat()
			);
		}
	}

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
		this.idurbaHelper = IdurbaHelper.getInstance(context.getDocumentModel());
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
					((StringType)attribute).addValidator(this);
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
