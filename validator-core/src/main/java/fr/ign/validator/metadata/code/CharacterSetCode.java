package fr.ign.validator.metadata.code;

import java.nio.charset.Charset;

import fr.ign.validator.metadata.code.internal.CodeList;
import fr.ign.validator.metadata.code.internal.CodeListValue;

/**
 * 
 * CharacterSet in metadata file
 * 
 * @author MBorne
 *
 */
public class CharacterSetCode extends CodeListValue {
	
	private static final CodeList CODE_LIST = CodeList.getCodeList("MD_CharacterSetCode");

	private CharacterSetCode(String value) {
		super(CODE_LIST, value);
	}

	public static CharacterSetCode valueOf(String code){
		if ( code == null ){
			return null;
		}
		return new CharacterSetCode(code);
	}
	
	/**
	 * Gets corresponding java charset
	 * 
	 * TODO improve mapping to support check and translate other codes
	 * 
	 * @see <a href="http://www.geoapi.org/snapshot/pending/org/opengis/metadata/identification/CharacterSet.html">CharacterSet in GeoAPI</a>
	 * 
	 * @return
	 */
	public Charset getCharset(){
		String charsetName = CODE_LIST.getTranslation(getValue());
		if ( null == charsetName ){
			return null;
		}
		return Charset.forName(charsetName);
	}
	
}
