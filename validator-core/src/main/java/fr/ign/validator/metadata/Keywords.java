package fr.ign.validator.metadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Implementation of gmd:MD_Keywords
 * 
 * Note that keywords are internationalized and grouped by thesaurus
 * 
 * TODO add thesaurusDate (it is required by INSPIRE)
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_Keywords.html">gmd:MD_Keywords</a>
 * 
 * @author MBorne
 *
 */
public class Keywords {

	private List<String> keywords ;
	
	/**
	 * ./gmd:thesaurusName/gmd:CI_Citation/gmd:title
	 */
	@JsonInclude(Include.NON_EMPTY)
	private String thesaurusName ;

	/**
	 * ./gmd:thesaurusName/gmd:CI_Citation/gmd:date/gmd:CI_Date/gmd:date/*
	 */
	private Date thesaurusDate ;
	
	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getThesaurusName() {
		return thesaurusName;
	}

	public void setThesaurusName(String thesaurusName) {
		this.thesaurusName = thesaurusName;
	}

	public Date getThesaurusDate() {
		return thesaurusDate;
	}

	public void setThesaurusDate(Date thesaurusDate) {
		this.thesaurusDate = thesaurusDate;
	}

	
}
