package fr.ign.validator.database;

import java.util.regex.Pattern;

/**
 * Validation of the table and column names concatenated in the SQL queries
 * (they come from the document model and, for the FeatureTypes created from the
 * data, from the headers of the user files).
 *
 * Only unquoted SQL identifiers are allowed (letters, digits and underscore,
 * not starting with a digit), which also makes them safe as file names (ex :
 * DATA/{TABLE}.csv).
 */
public class SqlIdentifier {

    private static final Pattern PATTERN = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");

    private SqlIdentifier() {
    }

    /**
     * @param name
     * @return true if name can be used as an unquoted SQL identifier
     */
    public static boolean isValid(String name) {
        return name != null && PATTERN.matcher(name).matches();
    }

    /**
     * @param name
     * @return name
     * @throws IllegalArgumentException if name is not a valid SQL identifier
     */
    public static String check(String name) {
        if (!isValid(name)) {
            throw new IllegalArgumentException(
                "Invalid name '" + name + "' (letters, digits and _ expected, not starting with a digit)"
            );
        }
        return name;
    }

}
