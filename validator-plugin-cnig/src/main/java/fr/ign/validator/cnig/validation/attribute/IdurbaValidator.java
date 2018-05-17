package fr.ign.validator.cnig.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.data.Attribute;
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
public class IdurbaValidator implements Validator<Attribute<String>> {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("IDURBA_VALIDATOR");

	/**
	 * idurbaHelper configured according to document model (see beforeMatching)
	 */
	private IdurbaHelper idurbaHelper;
	
	public IdurbaValidator(IdurbaHelper idurbaHelper){
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


}
