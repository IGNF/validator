package fr.ign.validator.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 
 * Singleton contenant la liste des types supportés (Boolean, Double, etc.)
 * 
 * Remarque : Ces types sont définis sous forme de services dans un fichier de ressource.
 * 
 * @see src/main/resources/META-INF/services/fr.ign.validator.model.AttributeType
 * 
 * @author MBorne
 *
 */
public class AttributeTypeRegistry {
	/**
	 * singleton's instance
	 */
	private static AttributeTypeRegistry instance = new AttributeTypeRegistry(); 
	/**
	 * liste des types connus
	 */
	private Map<String,AttributeType<?>> propotypes = new HashMap<String, AttributeType<?>>();
	
	/**
	 * Constructeur chargeant la liste des ValueType connus
	 */
	@SuppressWarnings("rawtypes")
	private AttributeTypeRegistry(){
		ServiceLoader<AttributeType> loader = ServiceLoader.load( AttributeType.class );
		for (AttributeType valueType : loader) {
			addPrototype(valueType);
		}
	}
	
	/**
	 * Récupération de la liste des types
	 * @return
	 */
	public Collection<String> getTypeNames() {
		return propotypes.keySet() ;
	}
	
	/**
	 * Récupération d'un type par son nom
	 * @param name
	 * @return
	 */
	public AttributeType<?> createAttributeTypeByName(String name){
		AttributeType<?> prototype = this.propotypes.get(name) ;
		return (AttributeType<?>)prototype.clone() ;
	}
	
	/**
	 * Enregistrement d'un type
	 * @param valueType
	 */
	private void addPrototype(AttributeType<?> valueType){
		this.propotypes.put(valueType.getTypeName(), valueType);
	}
	
	/**
	 * Récupération de l'instance ValueTypeRegistry
	 * @return
	 */
	public static AttributeTypeRegistry getInstance(){
		return instance ;
	}
	
}
