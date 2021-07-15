package fr.ign.validator.exception;

/**
 * Thrown when plugin is not found.
 */
public class PluginNotFoundException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    public PluginNotFoundException(String pluginName) {
        super(String.format("Plugin '%1s' not found", pluginName));
    }

}
