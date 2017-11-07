package fr.ign.validator.metadata.code;

import fr.ign.validator.metadata.code.internal.CodeList;
import fr.ign.validator.metadata.code.internal.CodeListValue;

/**
 * 
 * Implementation of gmd:MD_TopicCategoryCode
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_TopicCategoryCode.html">gmd:MD_TopicCategoryCode</a>
 * 
 * @author MBorne
 *
 */
public class TopicCategoryCode extends CodeListValue {
	
	private static final CodeList CODE_LIST = CodeList.getCodeList("MD_TopicCategory");

	private TopicCategoryCode(String value) {
		super(CODE_LIST, value);
	}

	public static TopicCategoryCode valueOf(String code){
		if ( code == null ){
			return null;
		}
		return new TopicCategoryCode(code);
	}

}
