package fr.ign.validator.error;

/**
 * Localisation de l'erreur
 * 
 * @warning Passage en majuscule des constantes pour valueOf
 *   (il faudra les remettre en CamelCase dans le rapport pour la compatiblité du GPU)
 * 
 * @author MBorne
 */
public enum ErrorScope {
	DIRECTORY("DIRECTORY"),
	HEADER("HEADER"),
	FEATURE("FEATURE")
	;
	
	private final String name;
	
	/**
     * @param name
     */
    private ErrorScope(final String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}

