package fr.ign.validator.report;

import java.util.HashMap;
import java.util.Map;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.report.ReportBuilder;



/**
 * 
 * Build filtered validation report
 * 
 * @author FCerizay
 *
 */
public class FilteredReportBuilder implements ReportBuilder {
	
	ReportBuilder reportBuilder;
	int maxError;
	
	Map<ErrorCode, Integer> countMap = new HashMap<ErrorCode, Integer>();
	
	/**
	 */
	public FilteredReportBuilder( ReportBuilder reportBuilder, int maxError ){
		this.reportBuilder = reportBuilder;
		this.maxError = maxError;
	}
	
	/**
	 * addError
	 */
	public void addError(Context context, ValidatorError error) {
		
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
		
		reportBuilder.addError(context, error);
	}

}
