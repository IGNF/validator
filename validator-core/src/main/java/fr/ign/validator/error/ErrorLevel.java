package fr.ign.validator.error;

/**
 * Localisation de l'erreur
 * 
 * @author MBorne
 */
public enum ErrorLevel {
	FATAL("FATAL"),
	ERROR("ERROR"),
	WARNING("WARNING"), 
	INFO("INFO"),
	DEBUG("DEBUG")
	;
	
	private final String name;
	
	/**
     * @param name
     */
    private ErrorLevel(final String name) {
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

