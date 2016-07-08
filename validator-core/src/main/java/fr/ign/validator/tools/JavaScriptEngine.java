package fr.ign.validator.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 
 * Moteur de script JavaScript (ECMA Script) reposant sur Rhino JS.
 * 
 * @author CBouche
 * 
 */
public class JavaScriptEngine {

	/**
	 * Le moteur de script
	 */
	private ScriptEngine rhinoEngine;

	
	/**
	 * Le code source du script
	 */
	private String scriptSource;

	
	/**
	 * 
	 */
	public JavaScriptEngine() {
		ScriptEngineManager engineManager = new ScriptEngineManager();
		rhinoEngine = engineManager.getEngineByName("js");
	}

	
	/**
	 * Exécute le script sur les variables en paramètres en renvoie le résultat renvoyé par le script
	 * @param scriptVariables
	 * @return
	 * @throws ScriptException
	 */
	public Object eval( Map<String, String> scriptVariables ) throws ScriptException {
		// reinitialisation des parametres
		Bindings bindings = rhinoEngine.getBindings( ScriptContext.ENGINE_SCOPE );
		bindings.clear();
		// reallocation des parametres
		for (String key : scriptVariables.keySet() ) {
			bindings.put(key , scriptVariables.get(key)) ;
		}
		return rhinoEngine.eval( scriptSource, bindings );
	}
	
	/**
	 * load script
	 * 
	 * @param scriptFile
	 * @throws FileNotFoundException
	 */
	public void loadScript(File scriptFile) throws FileNotFoundException {
		scriptSource = "";
		Scanner scanner = new Scanner(scriptFile);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			scriptSource += line;
		}
		scanner.close();
	}

	
	/**
	 * get script
	 * 
	 * @return
	 */
	public String getScript() {
		return scriptSource;
	}
	

	/**
	 * set script
	 * 
	 * @param script
	 */
	public void setScript(String script) {
		this.scriptSource = script;
	}

}
