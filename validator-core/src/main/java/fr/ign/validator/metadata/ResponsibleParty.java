package fr.ign.validator.metadata;

/**
 * Partial implementation of gmd:CI_ResponsibleParty
 * 
 * @see <a href=
 *      "http://www.datypic.com/sc/niem21/e-gmd_CI_ResponsibleParty.html">gmd:CI_ResponsibleParty</a>
 * 
 * @author MBorne
 *
 */
public class ResponsibleParty {

	private String organisationName;

	private String electronicMailAddress;

	private String role;

	private String phone;

	private String deliveryPoint;

	private String city;

	private String postalCode;

	private String country;

	private String onlineResourceUrl;

	private String hoursOfService;

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

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getDeliveryPoint() {
		return deliveryPoint;
	}

	public void setDeliveryPoint(String deliveryPoint) {
		this.deliveryPoint = deliveryPoint;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOnlineResourceUrl() {
		return onlineResourceUrl;
	}

	public void setOnlineResourceUrl(String onlineResourceUrl) {
		this.onlineResourceUrl = onlineResourceUrl;
	}

	public String getHoursOfService() {
		return hoursOfService;
	}

	public void setHoursOfService(String hoursOfService) {
		this.hoursOfService = hoursOfService;
	}

}
