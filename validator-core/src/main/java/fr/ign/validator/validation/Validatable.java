package fr.ign.validator.validation;

import fr.ign.validator.Context;

/**
 * 
 * Represents a validatable data
 * 
 * @author MBorne
 *
 */
public interface Validatable {
	
	/**
	 * Validates a data with a context
	 * @param context
	 */
	public void validate(Context context) throws Exception ;
	
}
