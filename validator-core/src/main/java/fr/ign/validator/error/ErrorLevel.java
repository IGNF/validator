package fr.ign.validator.error;

/**
 * ErrorLevel corresponding to the gravity of ValidatorError
 * 
 * @author MBorne
 */
public enum ErrorLevel {
	/**
	 * Validator has crashed
	 */
	FATAL("FATAL"),
	/**
	 * Data will probably crash integration process (invalid types, etc.)
	 */
	ERROR("ERROR"),
	/**
	 * Data is not perfect but integration is possible
	 */
	WARNING("WARNING"), 
	/**
	 * General information (visible by end users)
	 */
	INFO("INFO"),
	/**
	 * Debug information (hidden to end users)
	 */
	DEBUG("DEBUG")
	;
	
	private final String name;

	/**
     * @param name
     */
    private ErrorLevel(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}

