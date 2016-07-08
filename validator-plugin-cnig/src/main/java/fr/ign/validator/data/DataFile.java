package fr.ign.validator.data;

/**
 * Représente un fichier non géographique (PDF) de l'arborescence de fichier
 * @author CBouche
 *
 */
public class DataFile {
	/**
	 * Le nom du fichier
	 */
	private String name ;
	/**
	 * Le parent du fichier
	 */
	private String parent;
	
	/**
	 * Constructeur avec un nom
	 * @param name
	 */
	public DataFile(String name){
		this.name=name;
	}

	/**
	 * Récupération du nom
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Récupération du parent
	 * @return
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * Définition du parent
	 * @param parent
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}
}
