package fr.ign.validator.metadata;

/**
 * 
 * Partial implementation of gmd:CI_OnlineResource
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_CI_OnlineResource.html">gmd:CI_OnlineResource</a>
 * 
 * @author MBorne
 *
 */
public class OnlineResource {

	private String url ;
	
	private String protocol ;
	
	private String name ;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


}
