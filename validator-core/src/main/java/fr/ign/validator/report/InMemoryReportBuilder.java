package fr.ign.validator.report;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;

/**
 * 
 * Store ValidatorError in memory (designed for test purpose and UI).
 * 
 * @author MBorne
 *
 */
public class InMemoryReportBuilder implements ReportBuilder {

	/**
	 * Reported errors
	 */
	private List<ValidatorError> errors = new ArrayList<ValidatorError>();
	
	@Override
	public void addError(ValidatorError error) {
		errors.add(error);
	}
	
	/**
	 * Get reported errors
	 * @return
	 */
	public List<ValidatorError> getErrors(){
		return this.errors ;
	}
	
	/**
	 * Get reported errors for a given ErrorLevel
	 * @param code
	 * @return
	 */
	public List<ValidatorError> getErrorsByLevel(ErrorLevel level) {
		List<ValidatorError> result = new ArrayList<>();
		for (ValidatorError error : errors) {
			if ( error.getLevel().equals(level) ){
				result.add(error);
			}
		}
		return result;
	}


	/**
	 * Get reported errors for a given ErrorCode
	 * @param code
	 * @return
	 */
	public List<ValidatorError> getErrorsByCode(ErrorCode code) {
		List<ValidatorError> result = new ArrayList<>();
		for (ValidatorError error : errors) {
			if ( error.getCode().equals(code) ){
				result.add(error);
			}
		}
		return result;
	}
	
	
	/**
	 * Count errors
	 * @return
	 */
	public int countErrors(){
		return this.errors.size() ;
	}
	
	
	/**
	 * Count errors for a given code
	 * @param code
	 * @return
	 */
	public int countErrors(ErrorCode code){
		return getErrorsByCode(code).size();
	}

	
	/**
	 * Count errors for a given level
	 * @param level
	 * @return
	 */
	public int countErrors(ErrorLevel level){
		return getErrorsByLevel(level).size();
	}

	/**
	 * Check that report contains any ValidatorError with level = FATAL or level = ERROR
	 * @return
	 */
	public boolean isValid() {
		for (ValidatorError validatorError : errors) {
			if ( validatorError.getLevel().equals(ErrorLevel.FATAL) ){
				return false;
			}
			if ( validatorError.getLevel().equals(ErrorLevel.ERROR) ){
				return false;
			}
		}
		return true;
	}

}
