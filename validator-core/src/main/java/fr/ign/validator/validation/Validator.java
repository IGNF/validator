package fr.ign.validator.validation;

import fr.ign.validator.Context;

/**
 * 
 * Interface representing a validator for a validatable data
 * 
 * @author MBorne
 *
 * @param <T>
 */
public interface Validator<T> {
	
	/**
	 * Validates the element
	 * @param context
	 * @param validatable
	 */
	public void validate(Context context, T validatable) ;
	
}
