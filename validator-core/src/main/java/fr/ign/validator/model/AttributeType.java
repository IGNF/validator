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
 * Describes an attribute of a table (FeatureType)
 * 
 * @author MBorne
 *
 * @param <T> the matching java type
 */
@XmlJavaTypeAdapter(AttributeTypeAdapter.class)
public abstract class AttributeType<T> implements Model, Cloneable {
	/**
	 * Matching java class
	 */
	private Class<T> clazz ;
	
	/**
	 * Attribute name
	 */
	private String name ;
	/**
	 * Attribute definition (description)
	 */
	private String definition ;
	/**
	 * Regexp matching the attribute value
	 */
	private String regexp ;
	/**
	 * Limit size of the attribute
	 */
	private Integer size ;
	/**
	 * Indicates if the value is nullable
	 */
	private boolean nullable = false ;
	/**
	 * Restriction on a list of values
	 */
	private List<String> listOfValues ;

	/**
	 * Validators on attributes
	 */
	private List<Validator<Attribute<T>>> validators = new ArrayList<Validator<Attribute<T>>>() ;
	
	/**
	 * Constructing a class and validators by default
	 * @param clazz
	 */
	protected AttributeType(Class<T> clazz){
		this.clazz = clazz ;
		addValidator(new AttributeNullableValidator<T>());
		addValidator(new CharactersValidator<T>());
	}

	
	/**
	 * Returns type name
	 * 
	 * @return
	 */
	public abstract String getTypeName() ;
	
	/**
	 * Indicates if attribute is a geometry
	 * @return
	 */
	public boolean isGeometry(){
		return false ;
	}
	
	
	/**
	 * Converts a value in the matching java type.
	 * Validates the possibility of a conversion of a value in the java type matching the ValueType
	 * 
	 * @param value
	 * @return
	 */
	public abstract T bind(Object value) throws IllegalArgumentException ;
	
	/**
	 * Formats the value as a string parameter (e.g. YYYYMMDD for dates)
	 * 
	 * Note : null stays null
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException if type is incorrect
	 */
	public abstract String format(T value) throws IllegalArgumentException;
	
	/**
	 * Formats object in parameter
	 * 
	 * @param value
	 * @return
	 * @throws IllegalArgumentException if type is incorrect
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
	 * Gets value type for name
	 * 
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
	 * adds a validator
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

	    // returns the clone
	    return attributeType;
	}

	/**
	 * Creating attribute
	 * 
	 * @param object
	 * @return
	 */
	public Attribute<T> newAttribute(Object value) {
		return new Attribute<T>(this, value);
	}


}