package fr.ign.validator.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ign.validator.cnig.process.info.InfoWriter;

/**
 * Utilitaires pour le décode du champ IDURBA 
 * 
 * Format : [code INSEE ou numéro SIREN][date d'approbation]
 * 
 * Attention : on permet un _ en séparateur.
 * 
 * @author MBorne
 *
 */
public class IdurbaUtils {
	
	public static String getRegexp(){	
		// commune | 
		String result = "(";
		result += InseeUtils.REGEXP_COMMUNE ;
		result += "|";
		result += SirenUtils.REGEXP_SIREN ;
		result += ")";
		result += "(_?)" ;// Laxisme sur la présence d'un _
		// date d'approbation
		result += InfoWriter.REGEXP_DATE ;
		return result;
	}
	
	/**
	 * Renvoie l'expression régulière pour trouver IDURBA
	 * @param documentName
	 * @return
	 */
	public static String getRegexp(String documentName){
		Pattern pattern = Pattern.compile(InfoWriter.REGEXP_DU);
		Matcher matcher = pattern.matcher(documentName);
		
		if( matcher.matches()){
			String parts[] = documentName.split("_") ;
			return parts[0]+".*"+parts[2]+".*";
		}else{
			return null ;
		}
	}
	
	/**
	 * Validation d'un IDURBA
	 * @param idurba
	 * @return
	 */
	public static boolean isValid(String idurba){
		if ( null == idurba ){
			return false ;
		}
		return idurba.matches(getRegexp());
	}
	
	/**
	 * Validation IDURBA en fonction d'un nom de document
	 * @param idurba
	 * @param documentName
	 * @return
	 */
	public static boolean isValid(String idurba, String documentName){
		if ( null == idurba ){
			return false;
		}
		return idurba.matches(getRegexp(documentName));
	}

	
}
