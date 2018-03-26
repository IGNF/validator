package fr.ign.validator.metadata.code.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * Represents a list of values associated to translation. Values are loaded as a ResourceBundle
 * from src/main/resources/metadata/codes/[NAME].properties
 * 
 * @author MBorne
 *
 */
public class CodeList {

	private String name ;
	
	private ResourceBundle bundle ;
	
	private CodeList(String name){
		this.name = name;
		
		this.bundle = ResourceBundle.getBundle("metadata/codes/"+name);
	}

	public String getName() {
		return name;
	}

	/**
	 * Gets allowed values
	 * 
	 * @return
	 */
	public Collection<String> getAllowedValues(){
		List<String> keys = new ArrayList<>(bundle.keySet());
		Collections.sort(keys);
		return keys;
	}

	/**
	 * Gets translation for a given code
	 * 
	 * @param code
	 * @return
	 */
	public String getTranslation(String code){
		try {
			return this.bundle.getString(code);
		}catch (MissingResourceException e){
			return null;
		}
	}
	
	/**
	 * Gets a code list by a given name
	 * 
	 * @param name
	 * @return
	 */
	public static CodeList getCodeList(String name){
		return new CodeList(name);
	}

}
