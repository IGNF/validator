package fr.ign.validator.error;

/**
 * Error location. Allows to group errors while displaying validation report.
 *
 * @author MBorne
 */
public enum ErrorScope {
    /**
     * Error reported at document level (missing files, etc.).
     */
    DIRECTORY("DIRECTORY"),
    /**
     * Error reported on metadata file.
     */
    METADATA("METADATA"),
    /**
     * Error reported at table level (historically, columns definition validation.
     * Includes now global checks on a table such as "unique")
     */
    HEADER("HEADER"),
    /**
     * Error reported on a specific row of a table.
     */
    FEATURE("FEATURE");

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
