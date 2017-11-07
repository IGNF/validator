package fr.ign.validator.metadata.code;

import fr.ign.validator.metadata.code.internal.CodeList;
import fr.ign.validator.metadata.code.internal.CodeListValue;

/**
 * 
 * Implementation of gmd:LanguageCode
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_LanguageCode.html">gmd:LanguageCode</a>
 * 
 * @author MBorne
 *
 */
public class LanguageCode extends CodeListValue {
	
	private static final CodeList CODE_LIST = CodeList.getCodeList("LanguageCode");

	private LanguageCode(String value) {
		super(CODE_LIST, value);
	}

	public static LanguageCode valueOf(String code){
		if ( code == null ){
			return null;
		}
		return new LanguageCode(code);
	}

}
