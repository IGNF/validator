package fr.ign.validator.error;

/**
 * Error location, stands as the type of the error
 * @author MBorne
 */
public enum ErrorScope {
	/**
	 * document level (missing files, etc.)
	 */
	DIRECTORY("DIRECTORY"),
	/**
	 * Metadata file validation
	 */
	METADATA("METADATA"),
	/**
	 * Table header validation
	 */
	HEADER("HEADER"),
	/**
	 * Table row validation
	 */
	FEATURE("FEATURE")
	;

	private final String name;
	
	/**
     * @param name
     */
    private ErrorScope(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

