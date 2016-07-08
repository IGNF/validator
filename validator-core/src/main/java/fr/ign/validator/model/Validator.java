package fr.ign.validator.model;

import fr.ign.validator.Context;

/**
 * 
 * Interface représentant un validateur pour un élément validable
 * 
 * @author MBorne
 *
 * @param <T>
 */
public interface Validator<T> {
	
	/**
	 * Validation de l'élément
	 * @param context
	 * @param validatable
	 */
	public void validate(Context context, T validatable) ;
	
}
