package fr.ign.validator.data;

/**
 * 
 * Représente un fichier géographique de l'arborescence de fichier (couche géographique)
 * 
 * @author CBouche
 *
 */
public class DataLayer extends DataFile {


	/**
	 * String Bbox
	 */
	private String layerBbox="";

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
