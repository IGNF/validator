package fr.ign.validator.plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

/**
 * 
 * Utility class for plugin handling
 * 
 * @author MBorne
 *
 */
public class PluginManager {

	/**
	 * List of available plugins
	 */
	private List<Plugin> plugins = new ArrayList<>();
	
	public PluginManager(){
		ServiceLoader<Plugin> loader = ServiceLoader.load( Plugin.class );
		for (Plugin plugin : loader) {
			plugins.add(plugin);
		}
	}

	/**
	 * Gets the list with all the plugins
	 * @return
	 */
	public Collection<Plugin> getPlugins() {
		return plugins;
	}
	
	/**
	 * Gets a plugin by its name
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
