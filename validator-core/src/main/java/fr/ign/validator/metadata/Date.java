package fr.ign.validator.metadata;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.lang.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Date (2000-01-01) or DateTime (2000-01-01T12:00:00)
 * @see <a href="http://www.datypic.com/sc/niem21/e-gco_Date.html">gco:Date</a>
 * @see <a href="http://www.datypic.com/sc/niem21/e-gco_DateTime.html">gco:DateTime</a>
 */
public class Date implements Comparable<Date> {
	private String value;

	public Date(String value) {
		this.value = value; 
	}
	
	@JsonValue
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * FIXME test regular expression with sample dates 
	 * @return
	 */
	public boolean isValid() {
		if ( value == null ){
			return true;
		}
		try {
			return DatatypeFactory
			  .newInstance()
			  .newXMLGregorianCalendar(value)
			  .isValid();
		} catch (Exception e) {
			return false;
		}
	}


	@Override
	public String toString() {
		return value;
	}

	@Override
	public int compareTo(Date o) {
		return ObjectUtils.compare(value, o.getValue());
	}


}