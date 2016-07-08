package fr.ign.validator;

/**
 * Interface pour la création de plugin du validateur
 * 
 * Note : les plugins doivent être enregistrés dans le fichier
 *  src/main/resources/META-INF/services/fr.ign.validation.validator.Plugin
 * 
 * @see fr.ign.validation.plugin.DemoPlugin
 * @author MBorne
 *
 */
public interface Plugin {

	/**
	 * Renvoie le nom du plugin
	 * @return
	 */
	public String getName() ;
	
	/**
	 * Appelé par le validateur lors de l'initialisation du Plugin.
	 * @param validator
	 */
	public void setup( Context context ) ; 
	
}
