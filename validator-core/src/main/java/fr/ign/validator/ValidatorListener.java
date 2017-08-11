package fr.ign.validator;

import fr.ign.validator.data.Document;

/**
 * Permet d'ajouter des traitements au validateur
 * @author MBorne
 *
 */
public interface ValidatorListener {

	/**
	 * Appelé avant la mise en correspondance des fichiers 
	 *  avec le modèle
	 * @param context
	 */
	public void beforeMatching( Context context, Document document )  throws Exception ;
	
	/**
	 * Appelé après la mise en correspondance des fichiers 
	 *   et avant la validation
	 * @param context
	 */
	public void beforeValidate( Context context, Document document ) throws Exception  ;
	
	/**
	 * Appelé après la validation
	 * @param context
	 */
	public void afterValidate( Context context, Document document ) throws Exception ;
	
}
