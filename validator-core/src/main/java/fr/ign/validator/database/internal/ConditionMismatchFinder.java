package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;

/**
 * Condition Mismatch Finder - query the database to get all the features who
 * did not respect FeatureType conditions
 * 
 * @author cbouche
 *
 */
public class ConditionMismatchFinder {

    /**
     * hardcoded limit to avoid huge report on massively invalid table.
     */
    public static final int LIMIT_ERROR_COUNT = 10;

    /**
     * A precise reference to the id and the file where the constraint failed
     * 
     * @author cbouche
     */
    public class ConditionMismatch {
        public String id;
        public String file;

        public ConditionMismatch(String id, String file) {
            this.id = id;
            this.file = file;
        }
    }

    /**
     * 
     * @param database
     * @param tableName
     * @param condition
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<ConditionMismatch> findConditionMismatch(Database database, String tableName, String condition)
        throws SQLException, IOException {

    	// TODO validate condition to avoid SQL injection and crashes
    	// grammar validation with ANTLR v4
        String query = "SELECT __id, __file "
            + " FROM " + tableName
            + " WHERE NOT (" + condition + ")"
            + " LIMIT " + LIMIT_ERROR_COUNT;

        RowIterator it = database.query(query);

        List<ConditionMismatch> result = new ArrayList<ConditionMismatch>();
        while (it.hasNext()) {
            String[] row = it.next();
            result.add(new ConditionMismatch(row[0], row[1]));
        }
        it.close();
        return result;
    }

}
