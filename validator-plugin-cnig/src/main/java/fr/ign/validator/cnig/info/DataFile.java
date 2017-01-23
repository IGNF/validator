package fr.ign.validator.cnig.info;

/**
 * Représente un fichier non géographique (PDF) de l'arborescence de fichier
 * 
 * @author CBouche
 *
 */
public class DataFile {
	/**
	 * Le nom du fichier
	 */
	private String name;

	/**
	 * Constructeur avec un nom
	 * 
	 * @param name
	 */
	public DataFile(String name) {
		this.name = name;
	}

	/**
	 * Récupération du nom
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

}
