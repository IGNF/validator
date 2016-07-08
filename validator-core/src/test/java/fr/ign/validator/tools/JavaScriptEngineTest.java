package fr.ign.validator.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.junit.Test;

public class JavaScriptEngineTest {
	
	@Test
	public void testStrlenA() {
		File scriptFile = new File(getClass().getResource("/js/test-strlen-a.js").getPath()) ;
		if ( ! scriptFile.exists() ) {
			return ;
		}
		JavaScriptEngine jse = new JavaScriptEngine() ;
		try {
			jse.loadScript(scriptFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
			
		Map<String,String> variables = new HashMap<String, String>();
		variables.put("a", "LaChaine") ;
		try {
			Object result = jse.eval(variables) ;
			assertEquals( 8, result );
		} catch (ScriptException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	
}
