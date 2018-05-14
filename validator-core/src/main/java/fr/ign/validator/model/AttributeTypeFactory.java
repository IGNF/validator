package fr.ign.validator.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 
 * Prototype based factory providing AttributeType creation by name (Boolean, Double, etc.)
 * 
 * @see src/main/resources/META-INF/services/fr.ign.validator.model.AttributeType
 * 
 * @author MBorne
 *
 */
public class AttributeTypeFactory {

	private static AttributeTypeFactory instance = new AttributeTypeFactory(); 

	private Map<String,AttributeType<?>> propotypes = new HashMap<String, AttributeType<?>>();
	
	/**
	 * Construction loading AttributeType instances
	 */
	@SuppressWarnings("rawtypes")
	private AttributeTypeFactory(){
		ServiceLoader<AttributeType> loader = ServiceLoader.load( AttributeType.class );
		for (AttributeType valueType : loader) {
			addPrototype(valueType);
		}
	}
	
	/**
	 * Gets list of types
	 * @return
	 */
	public Collection<String> getTypeNames() {
		return propotypes.keySet() ;
	}
	
	/**
	 * Gets a type by name
	 * @param name
	 * @return
	 */
	public AttributeType<?> createAttributeTypeByName(String name){
		AttributeType<?> prototype = this.propotypes.get(name) ;
		return (AttributeType<?>)prototype.clone() ;
	}
	
	/**
	 * Saving a type
	 * @param valueType
	 */
	private void addPrototype(AttributeType<?> valueType){
		this.propotypes.put(valueType.getTypeName(), valueType);
	}
	
	/**
	 * Retrieves ValueTypeRegistry instance
	 * @return
	 */
	public static AttributeTypeFactory getInstance(){
		return instance ;
	}
	
}
