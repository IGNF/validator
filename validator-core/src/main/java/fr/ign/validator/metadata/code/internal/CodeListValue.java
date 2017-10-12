package fr.ign.validator.metadata.code.internal;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * Permissive enum for code list implementation
 * 
 * TODO add protected constructor with a CodeList
 * 
 * @author MBorne
 *
 */
public abstract class CodeListValue {
	/**
	 * Code list associated to the code
	 */
	private CodeList codeList ;
	/**
	 * ISO 19115 code for charset
	 */
	private String value;

	protected CodeListValue(CodeList codeList, String value){
		this.codeList = codeList;
		this.value = value;
	}

	public CodeList getCodeList() {
		return codeList;
	}
	
	/**
	 * True if the value belong to the code list
	 * @return
	 */
	public boolean isAllowedValue(){
		return getCodeList().getAllowedValues().contains(value);
	}

	@JsonValue
	public String getValue() {
		return value;
	}

	public void setValue(String code) {
		this.value = code;
	}
	
	@Override
	public int hashCode() {
		return value != null ? value.hashCode() : 0;
	}

	@Override
	public boolean equals(Object obj) {
		if ( obj == null ){
			return false;
		}
		return toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return this.value;
	}

}
