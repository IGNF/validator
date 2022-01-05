package fr.ign.validator.model;

import java.util.List;

/**
 * 
 * Feature Type Constraints
 * - attribute unicity ?
 * - foreign key ?
 * - sql conditions
 *
 * @author cbouche
 *
 */
public class FeatureTypeConstraints {
	
	private List<String> conditions;
	
	public FeatureTypeConstraints() {
		
	}

	public List<String> getConditions() {
		return conditions;
	}
	
	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

}
