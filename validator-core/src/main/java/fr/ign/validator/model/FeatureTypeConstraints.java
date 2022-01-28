package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.model.constraint.ForeignKeyConstraint;

/**
 * 
 * Feature Type Constraints 
 * foreign key 
 * conditions SQL queries
 *
 * @author cbouche
 *
 */
public class FeatureTypeConstraints {

	private List<ForeignKeyConstraint> foreignKeys = new ArrayList<ForeignKeyConstraint>();

    private List<String> conditions = new ArrayList<String>();
    
    public FeatureTypeConstraints() {
	}

    public FeatureTypeConstraints(@JsonProperty("foreignKeys") List<String> foreignKeys) {
    	if (foreignKeys != null) {
        	for (String foreignKey : foreignKeys) {
            	this.foreignKeys.add(ForeignKeyConstraint.parseForeignKey(foreignKey));
    		}    		
    	}
    }

    public List<String> getConditions() {
        return conditions;
    }

    public void setConditions(List<String> conditions) {
        this.conditions = conditions;
    }

	public List<ForeignKeyConstraint> getForeignKeys() {
		return foreignKeys;
	}

	public void setForeignKeys(List<ForeignKeyConstraint> foreignKeys) {
		this.foreignKeys = foreignKeys;
	}

}
