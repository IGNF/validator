package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Représente la structure d'une table de données géographiques.
 *  
 * @author MBorne
 */
@XmlRootElement
@XmlType(propOrder = { "typeName", "parentName", "description", "attributes" })
public class FeatureType implements Model {
	/**
	 * LE Catalogue associé au FeatureType
	 */
	private FeatureCatalogue featureCatalogue ;
	/**
	 * Parent (optionnel)
	 */
	private String parentName ;
	/**
	 * Le nom du type
	 */
	private String typeName ;
	/**
	 * La description
	 */
	private String description ;
	/**
	 * La liste des attributs
	 */
	private List<AttributeType<?>> attributes = new ArrayList<AttributeType<?>>();

	
	public FeatureType() {

	}

	public FeatureCatalogue getFeatureCatalogue() {
		return featureCatalogue;
	}

	@XmlTransient
	protected void setFeatureCatalogue(FeatureCatalogue featureCatalogue) {
		this.featureCatalogue = featureCatalogue;
	}

	/**
	 * Renvoie le type parent
	 * @return
	 */
	public String getParentName(){
		return this.parentName ;
	}
	/**
	 * Définit le type parent
	 * @param featureType
	 */
	@XmlElement(name="parent")
	public void setParentName(String parentName){
		this.parentName = parentName ;
	}

	/**
	 * Indique si le type a un parent
	 * @return
	 */
	public boolean hasParent(){
		return parentName != null ;
	}
	
	/**
	 * Renvoie le parent du FeatureType
	 * @return
	 */
	public FeatureType getParent(){
		if ( parentName == null ){
			return null ;
		}
		FeatureType parent = featureCatalogue.getFeatureTypeByName(parentName);
		if ( parent == null ){
			String message = String.format("Le FeatureType %1s définit comme parent de %2s n'existe pas",
				parentName,
				typeName
			);
			throw new RuntimeException(message);
		}
		return parent ;
	}
	/**
	 * Définit le parent du FeatureType
	 * @param featureType
	 */
	@XmlTransient
	public void setParent(FeatureType featureType){
		if ( featureType == null ){
			parentName = null ;
		}else{
			parentName = featureType.getTypeName() ;
		}
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
	 * Indique si la table est spatiale
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
	 * Ajout d'un attribut
	 * @param attribute
	 */
	public void addAttribute( AttributeType<?> attribute ){
		this.attributes.add(attribute);
	}
	
	/**
	 * Renvoie le nombre d'attributs (gère l'héritage)
	 * 
	 * @return
	 */
	public int getAttributeCount() {
		return getParentAttributeCount() + this.attributes.size() ;
	}
	/**
	 * Renvoie le nombre d'attribut du parent (0 si non définit)
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
	 * Renvoie un attribut par sa position (gère l'héritage)
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
	 * Renvoie un attribut par son nom (gère l'héritage)
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
	 * Renvoie les noms des attributs
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
	 * Recherche la position d'un attribut par son nom (gère l'héritage)
	 * @param name
	 * @return -1 si non défini
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