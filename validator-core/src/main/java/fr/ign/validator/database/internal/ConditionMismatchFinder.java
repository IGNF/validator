package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;

/**
 * Condition Mismatch Finder
 * - query the database to get all the features who did not respect FeatureType conditions
 * @author cbouche
 *
 */
public class ConditionMismatchFinder {

    /**
     * hardcoded limit to avoid huge report on massively invalid table.
     */
    public static final int LIMIT_ERROR_COUNT = 10;


    /**
     * 
     * @param database
     * @param tableName
     * @param condition
     * @return
     * @throws SQLException 
     * @throws IOException 
     */
	public List<String> findConditionMismatch(Database database, String tableName, String condition)
			throws SQLException, IOException {
        RowIterator it = database.query(
            "SELECT * "
                + " FROM " + tableName
                + " WHERE NOT (" + condition + ")"
                + " LIMIT " + LIMIT_ERROR_COUNT
        );

        List<String> result = new ArrayList<>();
        while (it.hasNext()) {
            String[] row = it.next();
            result.add(row[0]);
        }
        it.close();
        return result;
	}

}
