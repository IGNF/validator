package fr.ign.validator.metadata.code;

import fr.ign.validator.metadata.code.internal.CodeList;
import fr.ign.validator.metadata.code.internal.CodeListValue;

/**
 * Implementation of gmd:MD_ScopeCode
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_ScopeCode.html">gmd:MD_ScopeCode</a>
 * 
 * @author MBorne
 *
 */
public class ScopeCode extends CodeListValue {

	private static final CodeList CODE_LIST = CodeList.getCodeList("MD_ScopeCode");

	private ScopeCode(String value) {
		super(CODE_LIST, value);
	}

	public static ScopeCode valueOf(String code){
		if ( code == null ){
			return null;
		}
		return new ScopeCode(code);
	}

}
