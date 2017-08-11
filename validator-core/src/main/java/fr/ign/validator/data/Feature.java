package fr.ign.validator.data;

import fr.ign.validator.model.FeatureType;

/**
 * 
 * @author MBorne
 */
public class Feature {
	/**
	 * Description du type
	 */
	private FeatureType featureType ;
	/**
	 * Les attributs de la Feature
	 */
	private Attribute<?>[] attributes ;
	
	/**
	 * Construction d'une Feature avec des valeurs nulles
	 * @param featureType
	 */
	public Feature(FeatureType featureType){
		this.featureType = featureType ;
		this.attributes = new Attribute[featureType.getAttributeCount()];
		for ( int i = 0; i < featureType.getAttributeCount(); i++ ){
			Attribute<?> attribute = featureType.getAttribute(i).newAttribute(null);
			this.attributes[i] = attribute ;
		}
	}
	
	/**
	 * Renvoie le nombre d'attribut
	 * @return
	 */
	public int getAttributeCount() {
		return this.featureType.getAttributeCount() ;
	}

	/**
	 * Récupère une valeur d'attribut
	 * @param index
	 * @return
	 * @throws IndexOutOfBoundsException
	 */
	public Attribute<?> getAttribute(int index) throws IndexOutOfBoundsException {
		checkIndex(index);
		return this.attributes[index];
	}
	/**
	 * Récupère une valeur d'attribut par son nom
	 * @param name
	 * @return la valeur de l'attribut, null si non définie
	 */
	public Attribute<?> getAttribute(String name){
		int index = featureType.indexOf(name);
		if ( index < 0 ){
			return null ;
		}else{
			return attributes[index];
		}
	}
	
	/**
	 * Définit la valeur d'un attribut
	 * @param index
	 * @param value
	 */
	public void setAttribute(int index, Object value) throws IndexOutOfBoundsException {
		checkIndex(index);
		this.attributes[index].setValue( value ) ;
	}
	
	/**
	 * contrôle la validité d'un index
	 * @param index
	 * @throws IndexOutOfBoundsException
	 */
	private void checkIndex(int index) throws IndexOutOfBoundsException {
		if ( index >= getAttributeCount() ){
			throw new IndexOutOfBoundsException( String.format("invalid attribute index %1", index) );
		}
	}

	@Override
	public String toString() {
		String result = "" ;
		for ( int index = 0; index < featureType.getAttributeCount(); index++ ){
			if ( index != 0 ){
				result += ", " ;
			}
			result += featureType.getAttribute(index).getName()+"="+attributes[index] ;
		}
		return result ;
	}
}
