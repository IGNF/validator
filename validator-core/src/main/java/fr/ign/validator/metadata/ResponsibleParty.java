package fr.ign.validator.metadata;

/**
 * Partial implementation of gmd:CI_ResponsibleParty
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_CI_ResponsibleParty.html">gmd:CI_ResponsibleParty</a>
 * 
 * @author MBorne
 *
 */
public class ResponsibleParty {
	private String organisationName;
	// TODO Implement http://www.datypic.com/sc/niem21/e-gmd_CI_Address.html
	private String electronicMailAddress;
	private String role;

	public String getOrganisationName() {
		return organisationName;
	}
	public void setOrganisationName(String organisationName) {
		this.organisationName = organisationName;
	}

	public String getElectronicMailAddress() {
		return electronicMailAddress;
	}
	public void setElectronicMailAddress(String electronicMailAddress) {
		this.electronicMailAddress = electronicMailAddress;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
}
