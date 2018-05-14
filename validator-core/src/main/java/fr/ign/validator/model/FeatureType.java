package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Describe the content of a table
 * 
 * TODO restore XML mapping for parent as separated file (no more required for GPU, XML models is a flat export of a database)
 *  
 * @author MBorne
 */
@XmlRootElement
@XmlType(propOrder = { "typeName", "description", "attributes" })
public class FeatureType implements Model {
	/**
	 * Parent (optional)
	 */
	private FeatureType parent ;
	/**
	 * Type name
	 */
	private String typeName ;
	/**
	 * Description 
	 */
	private String description ;
	/**
	 * Attribute list
	 */
	private List<AttributeType<?>> attributes = new ArrayList<AttributeType<?>>();

	
	public FeatureType() {

	}

	/**
	 * Indicates if the FeatureType has a parent
	 * @return
	 */
	public boolean hasParent(){
		return parent != null ;
	}
	
	/**
	 * Gets parent of the FeatureType
	 * @return
	 */
	public FeatureType getParent(){
		return parent ;
	}
	/**
	 * Defines the parent of the FeatureType
	 * @param featureType
	 */
	@XmlTransient
	public void setParent(FeatureType parent){
		this.parent = parent ;
	}
	
	@Override
	public String getName() {
		return typeName ;
	}
		
	public String getTypeName() {
		return typeName;
	}

	@XmlElement
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	/**
	 * Indicates if table is spatial
	 * @return
	 */
	public boolean isSpatial(){
		for ( int i = 0; i < getAttributeCount(); i++ ){
			if ( getAttribute(i).isGeometry() ){
				return true ;
			}
		}
		return false ;
	}
	
	

	public String getDescription() {
		return description;
	}

	@XmlElement
	public void setDescription(String description) {
		this.description = description;
	}


		
	public List<AttributeType<?>> getAttributes() {
		return attributes;
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public void setAttributes(List<AttributeType<?>> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * Adds an attribute
	 * @param attribute
	 */
	public void addAttribute( AttributeType<?> attribute ){
		this.attributes.add(attribute);
	}
	
	/**
	 * Gets number of attributes (manages inheritance)
	 * 
	 * @return
	 */
	public int getAttributeCount() {
		return getParentAttributeCount() + this.attributes.size() ;
	}
	/**
	 * Return number of attributes of parent (0 if parentless)
	 * 
	 * @return
	 */
	private int getParentAttributeCount(){
		if ( ! hasParent() ){
			return 0 ;
		}else{
			return getParent().getAttributeCount() ;
		}
	}
	
	/**
	 * Gets an attribute by its position (manages inheritance)
	 * @param index
	 * @return
	 */
	public AttributeType<?> getAttribute( int index ){
		if ( index < 0 || index >= getAttributeCount() ){
			throw new IllegalArgumentException("argument index invalide (must be >= 0)") ;
		}
		if ( index < getParentAttributeCount() ) {
			return getParent().getAttribute(index);
		}else{
			return this.attributes.get( index - getParentAttributeCount() );	
		}
	}
	/**
	 * Gets an attribute by its name (manages inheritance)
	 * @param name
	 * @return
	 */
	public AttributeType<?> getAttribute( String name ){
		int index = indexOf(name) ;
		if ( index < 0 ){
			return null ;
		}
		return getAttribute(index);
	}
	
	/**
	 * Gets names of attributes
	 * @return
	 */
	public String[] getAttributeNames() {
		String[] result = new String[getAttributeCount()];
		for ( int i = 0; i < getAttributeCount(); i++ ){
			result[i] = getAttribute(i).getName() ;
		}
		return result ;
	}

	/**
	 * Finds the position of an attribute by its name (manages inheritance)
	 * @param name
	 * @return -1 si undefined
	 */
	public int indexOf(String name){
		String regexp = "(?i)"+name ;
		for (int i = 0; i < attributes.size(); i++) {
			if ( attributes.get(i).getName().matches(regexp) ){
				return getParentAttributeCount() + i ;
			}
		}
		if ( hasParent() ){
			return getParent().indexOf(name) ;
		}else{
			return -1 ;
		}
	}

	
	
}