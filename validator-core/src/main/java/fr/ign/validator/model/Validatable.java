package fr.ign.validator.model;

import fr.ign.validator.Context;

/**
 * 
 * Représente une données validable
 * 
 * @author MBorne
 *
 */
public interface Validatable {
	
	/**
	 * Validation d'une données en fonction d'un contexte
	 * @param context
	 */
	public void validate(Context context) throws Exception ;
	
}
