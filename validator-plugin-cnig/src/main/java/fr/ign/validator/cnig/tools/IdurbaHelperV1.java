package fr.ign.validator.cnig.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.ign.validator.cnig.model.DocumentName;

/**
 * 
 * Helper to validate IDURBA according to CNIG standards v2013 and v2014
 * 
 * @author MBorne
 *
 */
public class IdurbaHelperV1 extends IdurbaHelper {

	
	@Override
	public boolean isValid(String idurba){
		if ( null == idurba ){
			return false ;
		}
		return idurba.matches(getRegexp());
	}


	/**
	 * Get generic regexp for validation
	 * @return
	 */
	private String getRegexp(){	
		// municipality | 
		String result = DocumentName.REGEXP_INSEE_OR_SIREN;
		result += "(_?)" ;// optional _
		result += DocumentName.REGEXP_YYYYMMDD ;
		return result;
	}

	@Override
	public boolean isValid(String idurba, String documentName){
		if ( null == idurba ){
			return false;
		}
		return idurba.matches(getRegexp(documentName));
	}
	
	/**
	 * Gets regexp to find idUrba
	 * 
	 * @param documentName
	 * @return
	 */
	private String getRegexp(String documentName){
		Pattern pattern = Pattern.compile("(?i)"+DocumentName.getRegexpDU());
		Matcher matcher = pattern.matcher(documentName);
		
		if( matcher.matches()){
			String parts[] = documentName.split("_") ;
			return parts[0]+".*"+parts[2]+".*";
		}else{
			return null ;
		}
	}
	
	@Override
	public String getHelpFormat() {
		return "<INSEE/SIREN><DATAPPRO>";
	}
	
	@Override
	public String getHelpExpected(String documentName) {
		return getRegexp(documentName);
	}

}
