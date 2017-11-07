package fr.ign.validator.metadata;

/**
 * Partial implementation for gmd:report items focused on specification extraction from 
 * gmd:DQ_ConformanceResult.
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_report-1.html">gmd:report</a>
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_DQ_ConformanceResult.html">gmd:DQ_ConformanceResult</a>
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_specification-1.html">gmd:specification</a>
 * 
 * @author MBorne
 *
 */
public class Specification {
	
	/**
	 * Specification title (extracted from gmd:CI_Citation)
	 */
	private String title ;

	/**
	 * Specification date (extracted from gmd:CI_Citation)
	 */
	private Date date ;

	/**
	 * Specification date type (extracted from gmd:CI_Citation)
	 */
	private String dateType ;
	
	/**
	 * Validation result (true, false or text corresponding to gco:nilReason)
	 * 
	 * @warning CNIG guidance seems to specify something else than a boolean
	 *  
	 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_pass-1.html">gmd:pass</a>
	 */
	private String degree;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getDegree() {
		return degree;
	}

	public void setDegree(String degree) {
		this.degree = degree;
	}

}
