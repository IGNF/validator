package fr.ign.validator.cnig.info;

/**
 * Represents a non-geographical file (pdf) in directory
 * 
 * @author CBouche
 *
 */
public class DataFile {
	/**
	 * file name
	 */
	private String name;

	/**
	 * Constructor with name
	 * 
	 * @param name
	 */
	public DataFile(String name) {
		this.name = name;
	}

	/**
	 * Get name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

}
