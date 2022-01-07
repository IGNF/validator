package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Feature Type Constraints
 * - attribute unicity ?
 * - foreign key ?
 * - conditions: sql queries
 *
 * @author cbouche
 *
 */
public class FeatureTypeConstraints {
	
	private List<String> conditions = new ArrayList<String>();
	
	public FeatureTypeConstraints() {
		
	}

	public List<String> getConditions() {
		return conditions;
	}
	
	public void setConditions(List<String> conditions) {
		this.conditions = conditions;
	}

}
