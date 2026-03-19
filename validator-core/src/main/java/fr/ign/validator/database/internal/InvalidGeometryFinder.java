package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;

/**
 * Helper to find invalid geometries in a given table.
 *
 * @author DDArras
 *
 */
public class InvalidGeometryFinder {

    /**
     * hardcoded limit to avoid huge report on massively invalid table.
     */
    public static final int LIMIT_PER_ATTRIBUTE = 10;

    /**
     * A table with invalid geometries
     *
     * @author DDarras
     */
    public class InvalidGeometry {
        public String id;

        public InvalidGeometry(String id) {
            this.id = id;
        }
    }

    /**
     * Find invalid geometries for a given {tableName}.{columnName}. idColumnName
     * default is __id.
     *
     * @param database
     * @param tableName
     * @param columnName
     * @return values associated to the number of occurence
     * @throws SQLException
     * @throws IOException
     */
    public List<InvalidGeometry> findInvalidGeometries(Database database, String tableName, String columnName)
            throws SQLException, IOException {
        return findInvalidGeometries(database, tableName, columnName, "__id");
    }

    /**
     * Find invalid geometries for a given {tableName}.{columnName}, optional
     * idColumnName is used to specify the objects with invalid geometry.
     *
     * @param database
     * @param tableName
     * @param columnName
     * @param idColumnName
     * @return values associated to the number of occurence
     * @throws SQLException
     * @throws IOException
     */
    public List<InvalidGeometry> findInvalidGeometries(Database database, String tableName, String columnName,
            String idColumnName) throws SQLException, IOException {
        RowIterator it = database.query("SELECT " + idColumnName + " FROM " + tableName + " WHERE NOT ST_IsValid("
                + columnName + ") LIMIT " + LIMIT_PER_ATTRIBUTE);

        List<InvalidGeometry> result = new ArrayList<>();
        while (it.hasNext()) {
            String[] row = it.next();
            result.add(new InvalidGeometry(row[0]));
        }
        it.close();
        return result;
    }
}
