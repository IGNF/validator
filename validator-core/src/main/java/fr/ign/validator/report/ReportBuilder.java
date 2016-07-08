package fr.ign.validator.report;

import fr.ign.validator.Context;
import fr.ign.validator.error.ValidatorError;

/**
 * 
 * Build validation report
 * 
 * @author MBorne
 *
 */
public interface ReportBuilder {

	/**
	 * Add error to the report
	 * @param error
	 */
	public void addError(Context context, ValidatorError error);
	
}
