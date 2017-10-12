package fr.ign.validator.metadata;

/**
 * Partial implementation of gmd:MD_Format
 * 
 * @see <a href="http://www.datypic.com/sc/niem21/e-gmd_MD_Format.html">gmd:MD_Format</a>
 * 
 * @author MBorne
 *
 */
public class Format {
	
	/**
	 * Ex : Shapefile, SHP, etc.
	 */
	private String name;
	
	/**
	 * Ex : 1.0
	 */
	private String version;

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
