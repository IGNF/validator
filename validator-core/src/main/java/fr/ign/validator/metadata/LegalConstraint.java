package fr.ign.validator.metadata;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Implementation of gmd:MD_LegalConstraints
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_LegalConstraints.html">gmd:MD_LegalConstraints</a>
 * 
 * @author MBorne
 *
 */
public class LegalConstraint extends Constraint {
	
	/**
	 * TODO MD_RestrictionCode?
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> accessConstraints = new ArrayList<>();
	
	/**
	 * TODO MD_RestrictionCode?
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> useConstraints = new ArrayList<>();

	/**
	 * MD_RestrictionCode
	 */
	@JsonInclude(Include.NON_EMPTY)
	private List<String> otherConstraints = new ArrayList<>();
	
	@Override
	public String getType(){
		return "MD_LegalConstraints";
	}

	public List<String> getAccessConstraints() {
		return accessConstraints;
	}

	public void setAccessConstraints(List<String> accessConstraints) {
		this.accessConstraints = accessConstraints;
	}

	public List<String> getUseConstraints() {
		return useConstraints;
	}

	public void setUseConstraints(List<String> useConstraints) {
		this.useConstraints = useConstraints;
	}

	public List<String> getOtherConstraints() {
		return otherConstraints;
	}

	public void setOtherConstraints(List<String> otherConstraints) {
		this.otherConstraints = otherConstraints;
	}
	
}
