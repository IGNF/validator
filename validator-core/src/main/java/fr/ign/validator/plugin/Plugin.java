package fr.ign.validator.plugin;

import fr.ign.validator.Context;

/**
 * 
 * Plugin allows to change and extend the default validator behavior
 * 
 * Note that 
 * 
 * <ul>
 * 	<li>plugin must be defined in the following file : src/main/resources/META-INF/services/fr.ign.validation.validator.Plugin</li>
 *  <li>plugin must be explicitely loaded in CLI (--plugins)</li>
 * <ul>
 * 
 * @see validator-cnig-plugin (extension for CNIG profiles relative to www.geoportail-urbanisme.gouv.fr)
 * 
 * @author MBorne
 *
 */
public interface Plugin {

	/**
	 * Get plugin name
	 * @return
	 */
	public String getName() ;
	
	/**
	 * Invoked when the plugin is loaded (allows the plugin to extend models, to register listeners, etc.)
	 * @param context
	 */
	public void setup( Context context ) ; 
	
}
