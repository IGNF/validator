package fr.ign.validator.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 
 * Classe utilitaire pour la manipulation des plugins
 * 
 * @author MBorne
 *
 */
public class PluginManager {

	/**
	 * Liste des plugins disponibles
	 */
	private List<Plugin> plugins = new ArrayList<>();
	
	public PluginManager(){
		ServiceLoader<Plugin> loader = ServiceLoader.load( Plugin.class );
		for (Plugin plugin : loader) {
			plugins.add(plugin);
		}
	}

	/**
	 * Renvoie la liste de tous les plugins
	 * @return
	 */
	public Collection<Plugin> getPlugins() {
		return plugins;
	}
	
	/**
	 * Renvoie un plugin par son nom
	 * @param name
	 * @return
	 */
	public Plugin getPluginByName(String name){
		for (Plugin plugin : plugins) {
			if ( plugin.getName().equals(name) ){
				return plugin;
			}
		}
		return null;
	}

}
