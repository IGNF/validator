package fr.ign.validator.tools;

import java.util.HashMap;
import java.util.Map;

public class ReferenceActeSupTools {
	
	
	public static Map<String, String> leftjoin(Map<String, String> rows, Map<String, String> leftrows) {
		Map<String, String> joinColumn = new HashMap<String, String>() ;
		
		/*
		 * rows
		 * - primaryKey
		 * - foreignKey
		 * 
		 * OR
		 * 
		 * rows
		 * - primaryKey
		 * - foreignKey1 | foreignKey2 | foreignKey3
		 * 
		 * leftRows
		 * - primaryKey
		 * - Value
		 * 
		 * leftRows.primaryKey == rows.foreignKey ?
		 * 
		 * joinColumn
		 * - primaryKey
		 * - value
		 */
		for (String primaryKey : rows.keySet()) {
			
			String[] foreignKeys = readValue(rows.get(primaryKey)) ;
			
			String joinedValues = getValues(leftrows, foreignKeys) ;
			
			joinColumn.put(primaryKey, joinedValues) ;
		}
		
		
		return joinColumn ;
	}
	
	private static String[] readValue(String value) {
		return value.split("\\|") ;
	}
	
	private static String getValues(Map<String, String> rows, String[] keys) {
		String joinedValues = "" ;
		if (keys.length == 0) {
			return joinedValues ;
		}
		for (int i = 0; i < keys.length; i++) {
			if (! rows.containsKey(keys[i])) {
				continue ;
			}
			if (joinedValues.isEmpty()) {
				joinedValues = rows.get(keys[i]) ;
				continue ;
			}
			joinedValues = joinedValues + "|" + rows.get(keys[i]) ;
		}
		return joinedValues ;
	}
}
