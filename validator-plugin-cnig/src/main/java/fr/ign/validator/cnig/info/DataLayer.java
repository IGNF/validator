package fr.ign.validator.cnig.info;

/**
 * 
 * Represents a geographical file in directory (geographical layer)
 * 
 * @author CBouche
 *
 */
public class DataLayer extends DataFile {

	/**
	 * String Bbox
	 * 
	 * TODO typer en Enveloppe
	 */
	private String layerBbox = "";

	/**
	 * @param name
	 */
	public DataLayer(String name) {
		super(name);
	}

	public String getLayerBbox() {
		return layerBbox;
	}

	public void setLayerBbox(String layerBbox) {
		this.layerBbox = layerBbox;
	}

}
