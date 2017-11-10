package fr.ign.validator.metadata;

/**
 * Implementation of gmd:MD_SecurityConstraints
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_SecurityConstraints.html">gmd:MD_SecurityConstraints</a>
 * 
 * @author MBorne
 *
 */
public class SecurityConstraint extends Constraint {
	
	/**
	 * TODO MD_ClassificationCode?
	 */
	private String classification ;

	@Override
	public String getType(){
		return "MD_SecurityConstraints";
	}
	
	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

}
