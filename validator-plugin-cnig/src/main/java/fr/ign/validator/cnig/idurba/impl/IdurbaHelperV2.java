package fr.ign.validator.cnig.idurba.impl;

import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.cnig.utils.DocumentNameHelper;

/**
 * 
 * Helper to validate IDURBA according to CNIG standards v2013 and v2014
 * 
 * @author MBorne
 *
 */
public class IdurbaHelperV2 extends IdurbaHelper {

	@Override
	public boolean isValid(String idurba){
		if ( null == idurba ){
			return false ;
		}
		return idurba.matches(DocumentNameHelper.REGEXP_DU);
	}

	@Override
	public boolean isValid(String idurba, String documentName){
		if ( null == idurba ){
			return false;
		}
		return idurba.equalsIgnoreCase(documentName);
	}

	@Override
	public String getHelpFormat() {
		return "<INSEE/SIREN>_<TYPEDOC>_<DATAPPRO>{_CodeDU}";
	}	

	@Override
	public String getHelpExpected(String documentName) {
		return documentName;
	}

}
