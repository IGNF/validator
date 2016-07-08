package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

/**
 * Contient une liste de FeatureType et permet de les retrouver par leur nom
 * 
 * Note : Sert d'indirection pour la gestion des héritages (les parents sont définit par leur nom)
 * 
 * @author MBorne
 */
public class FeatureCatalogue {
	
	/**
	 * La liste des FeatureType
	 */
	private List<FeatureType> featureTypes = new ArrayList<FeatureType>() ;
	

	public FeatureCatalogue(){
		
	}
	
	/**
	 * Renvoie la liste des FeatureType
	 * @return
	 */
	@XmlTransient
	public List<FeatureType> getFeatureTypes() {
		return featureTypes;
	}
	/**
	 * Définit la liste des FeatureTypes
	 * @param featureTypes
	 */
	public void setFeatureTypes(List<FeatureType> featureTypes) {
		this.featureTypes = featureTypes;
	}
	/**
	 * Ajoute un FeatureType
	 * @param featureType
	 */
	public void addFeatureType(FeatureType featureType){
		featureType.setFeatureCatalogue(this);
		this.featureTypes.add(featureType);
	}
	/**
	 * Trouve un FeatureType à l'aide de son nom
	 * @return null si non trouvé
	 */
	public FeatureType getFeatureTypeByName(String typeName) {
		for (FeatureType featureType : featureTypes) {
			if ( featureType.getTypeName().equals(typeName) ){
				return featureType ;
			}
		}
		return null ;
	}
	
}
