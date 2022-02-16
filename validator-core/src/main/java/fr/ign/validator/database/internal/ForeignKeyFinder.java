package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.model.constraint.ForeignKeyConstraint;

/**
 * Foreign Key Finder Provide a method to look out for foreign key not found
 * 
 * @author cbouche
 *
 */
public class ForeignKeyFinder {

    public class ForeignKeyMismatch {
        public String file;
        public String id;
        public String values;

        public ForeignKeyMismatch(String id, String file, String values) {
            this.id = id;
            this.file = file;
            this.values = values;
        }
    }

    /**
     * Retreive all Foreign Key Mismatch by perfoming an SQL query to database
     * 
     * @param database
     * @param tableName
     * @param foreignKey
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<ForeignKeyMismatch> foreignKeyNotFound(
        Database database,
        String tableName,
        ForeignKeyConstraint foreignKey) throws SQLException, IOException {

        // TODO validate condition to avoid SQL injection and crashes
        // grammar validation with ANTLR v4

        List<String> conditions = new ArrayList<String>();
        for (int i = 0; i < foreignKey.getSourceColumnNames().size(); i++) {
            String condition = "a." + foreignKey.getSourceColumnNames().get(i)
                + " LIKE "
                + "b." + foreignKey.getTargetColumnNames().get(i);
            conditions.add(condition);
        }

        // Query exemple
        // sub request retrieve all foreign key match
        // SELECT r.__id, r.__file, TYPEPSC, STYPEPSC
        // FROM PRESCRIPTION_SURF AS r WHERE r.__id NOT IN (
        // SELECT a.__id FROM PRESCRIPTION_SURF AS a
        // JOIN PrescriptionUrbaType AS b
        // ON (a.TYPEPSC LIKE b.TYPEPSC AND a.STYPEPSC LIKE b.STYPEPSC) )

        String query = "SELECT r.__id, r.__file, "
            + String.join(", ", foreignKey.getSourceColumnNames())
            + " FROM " + tableName + " AS r"
            + " WHERE r.__id NOT IN ("
            + " SELECT a.__id "
            + " FROM " + tableName + " AS a"
            + " JOIN " + foreignKey.getTargetTableName() + " AS b"
            + " ON (" + String.join(" AND ", conditions) + ")"
            + " )";
        RowIterator it = database.query(query);

        List<ForeignKeyMismatch> result = new ArrayList<ForeignKeyMismatch>();
        while (it.hasNext()) {
            String[] row = it.next();
            String values = "";
            if (row.length > 2) {
                values = String.join(", ", Arrays.copyOfRange(row, 2, row.length));
            }
            result.add(new ForeignKeyMismatch(row[0], row[1], values));
        }
        it.close();
        return result;
    }

}
