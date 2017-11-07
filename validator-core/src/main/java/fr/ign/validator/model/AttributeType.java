package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.attribute.AttributeNullableValidator;
import fr.ign.validator.validation.attribute.CharactersValidator;
import fr.ign.validator.xml.binding.AttributeTypeAdapter;


/**
 * 
 * Décrit un champ d'un table (FeatureType)
 * 
 * @author MBorne
 *
 * @param <T> le type java correspondant
 */
@XmlJavaTypeAdapter(AttributeTypeAdapter.class)
public abstract class AttributeType<T> implements Model, Cloneable {
	/**
	 * Classe Java correspondante
	 */
	private Class<T> clazz ;
	
	/**
	 * Le nom de l'attribut
	 */
	private String name ;
	/**
	 * La définition de l'attribut (description)
	 */
	private String definition ;
	/**
	 * L'expression régulière correspondant à la valeur de l'attribut.
	 */
	private String regexp ;
	/**
	 * La taille limite de l'attribut
	 */
	private Integer size ;
	/**
	 * Indique si la valeur nulle est autorisée.
	 */
	private boolean nullable = false ;
	/**
	 * Restriction sur une liste de valeur
	 */
	private List<String> listOfValues ;

	/**
	 * Validateurs sur les attributs
	 */
	private List<Validator<Attribute<T>>> validators = new ArrayList<Validator<Attribute<T>>>() ;
	
	/**
	 * Constructeur avec une classe et les validateurs par défaut
	 * @param clazz
	 */
	protected AttributeType(Class<T> clazz){
		this.clazz = clazz ;
		addValidator(new AttributeNullableValidator<T>());
		addValidator(new CharactersValidator<T>());
	}

	
	/**
	 * Renvoie le nom du type
	 * @return
	 */
	public abstract String getTypeName() ;
	
	/**
	 * Indique si l'attribut est une géométrie
	 * @return
	 */
	public boolean isGeometry(){
		return false ;
	}
	
	
	/**
	 * Convertit une valeur dans le type java correspondant. Valide la possibilité de convertir 
	 * une valeur dans le type java correspondant à ValueType
	 * 
	 * @param value
	 * @return
	 */
	public abstract T bind(Object value) throws IllegalArgumentException ;
	
	/**
	 * Formatte la valeur en paramètre sous forme d'une chaîne de caractères (ex YYYYMMDD pour les dates)
	 * 
	 * Attention : null reste null
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException si le type n'est pas correct
	 */
	public abstract String format(T value) throws IllegalArgumentException;
	
	/**
	 * Formatte l'objet en paramètre
	 * @param value
	 * @return
	 * @throws IllegalArgumentException si le type n'est pas correct
	 */
	public String formatObject(Object value) throws IllegalArgumentException {
		if ( value == null ){
			return null ;
		}
		if ( this.clazz.isAssignableFrom(value.getClass()) ){
			return format( this.clazz.cast(value) ) ;
		}else{
			throw new IllegalArgumentException(String.format(
				"Invalid type {} for value"
			));
		}
	}
	
	/**
	 * Get value type for name
	 * @param name
	 * @return
	 */
	public static AttributeType<?> forName(String name){
		return AttributeTypeFactory.getInstance().createAttributeTypeByName(name) ;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean hasRegexp(){
		return null != regexp ;
	}
	
	public String getRegexp() {
		return regexp;
	}

	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}


	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
	
	
	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}


	public boolean hasListOfValues(){
		return listOfValues != null ;
	}

	public List<String> getListOfValues() {
		return listOfValues;
	}

	public void setListOfValues(List<String> listOfValues) {
		this.listOfValues = listOfValues;
	}

	/**
	 * Ajout d'un validateur
	 * @param validator
	 */
	public void addValidator(Validator<Attribute<T>> validator) {
		this.validators.add(validator);
	}

	/**
	 * @return the validators
	 */
	public List<Validator<Attribute<T>>> getValidators(){
		return this.validators ;
	}

	
	@Override
	public String toString() {
		String result = name+ " ("+getClass().getSimpleName()+")" ;
		return result ;
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		AttributeType<T> attributeType = null;
	    try {
	      	attributeType = (AttributeType<T>) super.clone();
	      	attributeType.validators = new ArrayList<Validator<Attribute<T>>>(validators.size());
	      	attributeType.validators.addAll(validators);
	    } catch(CloneNotSupportedException cnse) {
	      	throw new RuntimeException(cnse);
	    }

	    // on renvoie le clone
	    return attributeType;
	}

	/**
	 * Création d'un attribut
	 * @param object
	 * @return
	 */
	public Attribute<T> newAttribute(Object value) {
		return new Attribute<T>(this, value);
	}


}