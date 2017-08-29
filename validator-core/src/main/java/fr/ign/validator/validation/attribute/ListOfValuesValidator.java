package fr.ign.validator.validation.attribute;

import java.util.List;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation en fonction d'une liste de valeur
 * 
 * @author MBorne
 *
 */
public class ListOfValuesValidator implements Validator<Attribute<String>> {

	@Override
	public void validate(Context context, Attribute<String> attribute) {
		String value = attribute.getBindedValue() ;
		
		if ( value == null ){
			return ;
		}
		
		if ( ! attribute.getType().hasListOfValues() ){
			return ;
		}

		/*
		 * recherche de l'existence de la valeur
		 */
		for ( String string : attribute.getType().getListOfValues() ) {
			if ( string.equals(value) ){
				return ; 
			}
		}
		
		context.report(
			ErrorCode.ATTRIBUTE_UNEXPECTED_VALUE, 
			value,
			formatListOfValues( attribute.getType().getListOfValues() )
		);
	}

	/**
	 * Mise en forme d'une liste de valeur pour le rapport d'erreur
	 * @param listOfValues
	 * @return
	 */
	private Object formatListOfValues(List<String> listOfValues) {
		StringBuilder sb = new StringBuilder();
		boolean first = true ;
		for (String string : listOfValues) {
			if ( ! first ){
				sb.append(", ");
			}else{
				first = false ;
			}
			sb.append(string);
		}
		return sb.toString();
	}
	

}
