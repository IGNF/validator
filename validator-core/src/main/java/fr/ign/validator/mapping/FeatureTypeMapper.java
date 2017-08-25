package fr.ign.validator.mapping;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;

/**
 * Effectue une mise en correspondance entre une entête et les attributs d'un FeatureType
 * 
 * @author MBorne
 */
public class FeatureTypeMapper {
	/**
	 * L'entête de la table en entrée
	 */
	private String[] columns ;
	/**
	 * Le modèle de données en sortie
	 */
	private FeatureType featureType ;
	/**
	 * La position dans l'entête de chacun des champs du FeatureType
	 */
	private int[] attributeIndexes ;
	/**
	 * La liste des attributs présent dans l'entête et pas dans la données
	 */
	private List<String> unexpectedAttributes = new ArrayList<String>() ;
	
	/**
	 * 
	 * @param columns
	 * @param featureType
	 */
	public FeatureTypeMapper(String[] columns, FeatureType featureType){
		this.featureType = featureType ;
		this.columns = columns ;
		buildMapping(); 
	}
	
	/**
	 * Construction d'une instance à partir d'une entête de table et d'un FeatureType
	 * @param columns
	 * @param featureType
	 * @return
	 */
	public static FeatureTypeMapper createMapper(String[] columns, FeatureType featureType){
		return new FeatureTypeMapper(columns, featureType) ;
	}
	
	
	/**
	 * @return the featureType
	 */
	public FeatureType getFeatureType() {
		return featureType;
	}

	/**
	 * @return the header
	 */
	public String[] getColumns() {
		return columns;
	}
	
	/**
	 * Renvoie la liste des attributs présents dans la données mais pas dans le modèle
	 * @return
	 */
	public List<String> getUnexpectedAttributes(){
		return this.unexpectedAttributes ; 
	}
	
	/**
	 * Renvoie la liste des attributs présents dans le modèle (FeatureType) mais pas dans la données (header)
	 * @return
	 */
	public List<String> getMissingAttributes(){
		List<String> missingAttributes = new ArrayList<String>() ;
		for ( int index = 0; index < attributeIndexes.length; index++ ) {
			if ( attributeIndexes[index] < 0 ){
				AttributeType<?> missingAttribute = getFeatureType().getAttribute(index) ;
				missingAttributes.add(missingAttribute.getName());
			}
		}
		return missingAttributes ;
	}
	
	/**
	 * Calcul du mapping
	 */
	private void buildMapping(){
		unexpectedAttributes.clear(); 
		/*
		 * Calcul de la position des attributs dans la table
		 */
		attributeIndexes = new int[ getFeatureType().getAttributeCount() ];
		for ( int i = 0; i < attributeIndexes.length; i++ ){
			attributeIndexes[i] = -1 ;
		}
		for ( int i = 0; i < columns.length; i++ ){
			String name = columns[i] ;
			int index = getFeatureType().indexOf(name) ;
			if ( index < 0 ){
				// L'attribut est définit dans la source, pas dans la cible
				unexpectedAttributes.add(name);
			}else{
				attributeIndexes[index] = i ;
			}
		}
	}

	/**
	 * Renvoie la position d'un attribut du FeatureType dans l'entête
	 * @param index
	 * @return -1 si absent
	 */
	public int getAttributeIndex(int index) {
		return attributeIndexes[index] ;
	}
	
}
