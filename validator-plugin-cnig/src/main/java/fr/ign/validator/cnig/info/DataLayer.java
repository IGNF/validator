package fr.ign.validator.cnig.info;

import com.vividsolutions.jts.geom.Envelope;

/**
 * 
 * Represents a geographical file in directory (geographical layer)
 * 
 * @author CBouche
 *
 */
public class DataLayer extends DataFile {

	private Envelope boundingBox = new Envelope();

	/**
	 * @param name
	 */
	public DataLayer(String name) {
		super(name);
	}

	public Envelope getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(Envelope boundingBox) {
		this.boundingBox = boundingBox;
	}

}
