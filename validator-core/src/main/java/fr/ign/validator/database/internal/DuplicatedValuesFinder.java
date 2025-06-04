package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;

/**
 * Helper to find duplicated values in a given database.
 *
 * @author MBorne
 *
 */
public class DuplicatedValuesFinder {

    /**
     * hardcoded limit to avoid huge report on massively invalid table.
     */
    public static final int LIMIT_PER_ATTRIBUTE = 10;

    /**
     * A duplicated value with the number of occurence
     *
     * @author MBorne
     */
    public class DuplicatedValue {
        public String value;
        public int count;

        public DuplicatedValue(String value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    /**
     * Find duplicated values for a given {tableName}.{columnName}
     *
     * @param database
     * @param tableName
     * @param columnName
     * @return values associated to the number of occurence
     * @throws SQLException
     * @throws IOException
     */
    public List<DuplicatedValue> findDuplicatedValues(Database database, String tableName, String columnName)
        throws SQLException, IOException {
        RowIterator it = database.query(
            "SELECT " + columnName + " AS id, count(*) AS count"
                + " FROM " + tableName
                + " WHERE " + columnName + " IS NOT NULL"
                + " GROUP BY " + columnName
                + " HAVING count(*) > 1 ORDER BY count(*) DESC"
                + " LIMIT " + LIMIT_PER_ATTRIBUTE
        );

        List<DuplicatedValue> result = new ArrayList<>();
        while (it.hasNext()) {
            String[] row = it.next();
            result.add(
                new DuplicatedValue(
                    row[0],
                    Integer.parseInt(row[1])
                )
            );
        }
        it.close();
        return result;
    }
}
