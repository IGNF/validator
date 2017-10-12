package fr.ign.validator.report;

import java.util.HashMap;
import java.util.Map;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ValidatorError;

/**
 * 
 * Decorate a ReportBuilder to limit the maximum number of error reported for each type 
 * (avoid to handle large file parsing)
 * 
 * @author FCerizay
 *
 */
public class FilteredReportBuilder implements ReportBuilder {
	/**
	 * Original report builder
	 */
	private ReportBuilder original;
	/**
	 * Maximum number of error for each type
	 */
	private int maxError;
	/**
	 * Count map
	 */
	private Map<ErrorCode, Integer> countMap = new HashMap<ErrorCode, Integer>();

	/**
	 * Constructor with an existing reportBuilder
	 * @param original
	 * @param maxError
	 */
	public FilteredReportBuilder( ReportBuilder original, int maxError ){
		this.original = original;
		this.maxError = maxError;
	}
	
	@Override
	public void addError(ValidatorError error) {
		ErrorCode errorCode = error.getCode();
		
		Integer count = countMap.get(errorCode);
		if ( count == null ){
			count = 0;
		}
		count++;

		countMap.put(errorCode, count);
		if ( count > this.maxError ){
			return;
		}
		
		original.addError(error);
	}

}
