package fr.ign.validator.tools;

/**
 * Helper to manipulate column names.
 *
 * @author MBorne
 *
 */
public class HeaderHelper {

    /**
     * Find the position of a given columnName in an header (case insensitive).
     *
     * @param header
     * @param columnName
     * @return The index of the column, -1 if not found
     */
    public static int findColumn(String[] header, String columnName) {
        String regexp = "(?i)" + columnName;
        for (int index = 0; index < header.length; index++) {
            if (header[index].matches(regexp)) {
                return index;
            }
        }
        return -1;
    }

}
