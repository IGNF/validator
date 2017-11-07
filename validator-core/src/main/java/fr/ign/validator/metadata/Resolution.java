package fr.ign.validator.metadata;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Implementation of gmd:MD_Resolution
 *
 * Note that either denominator or distance should be defined
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_Resolution.html">gmd:MD_Resolution</a>
 *  
 * @author MBorne
 *
 */
public class Resolution {

	@JsonInclude(Include.NON_EMPTY)
	private String denominator;
	
	@JsonInclude(Include.NON_EMPTY)
	private String distance ;

	public String getDenominator() {
		return denominator;
	}

	public void setDenominator(String denominator) {
		this.denominator = denominator;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	
}
