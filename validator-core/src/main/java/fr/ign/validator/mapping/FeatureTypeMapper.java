package fr.ign.validator.mapping;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;

/**
 * Maps header with attributes of a FeatureType
 * 
 * @author MBorne
 */
public class FeatureTypeMapper {
	/**
	 * table header (input)
	 */
	private String[] columns ;
	/**
	 * data model (output)
	 */
	private FeatureType featureType ;
	/**
	 * Location in header of each FeatureType field
	 */
	private int[] attributeIndexes ;
	/**
	 * List of attributes existing in header (data) and missing in model (FeatureType)
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
	 * Constructing an instance from header and FeatureType
	 * 
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
	 * Returns a list of attributes existing in header and missing in model
	 * 
	 * @return
	 */
	public List<String> getUnexpectedAttributes(){
		return this.unexpectedAttributes ; 
	}
	
	/**
	 * Returns a list of attributes existing in model and missing in header
	 * 
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
	 * Creates a mapping
	 */
	private void buildMapping(){
		unexpectedAttributes.clear(); 
		/*
		 * Finding the position of each attribute in the table
		 */
		attributeIndexes = new int[ getFeatureType().getAttributeCount() ];
		for ( int i = 0; i < attributeIndexes.length; i++ ){
			attributeIndexes[i] = -1 ;
		}
		for ( int i = 0; i < columns.length; i++ ){
			String name = columns[i] ;
			int index = getFeatureType().indexOf(name) ;
			if ( index < 0 ){
				// Attribute defined in source, not in target
				unexpectedAttributes.add(name);
			}else{
				attributeIndexes[index] = i ;
			}
		}
	}

	/**
	 * Returns the position of an attribute of the FeatureType (in header)
	 * 
	 * @param index
	 * @return -1 si absent
	 */
	public int getAttributeIndex(int index) {
		return attributeIndexes[index] ;
	}
	
}
