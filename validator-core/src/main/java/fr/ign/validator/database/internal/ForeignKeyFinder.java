package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.TableModel;
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
     * @param tableModel
     * @param foreignKey
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<ForeignKeyMismatch> foreignKeyNotFound(
        Database database,
        TableModel tableModel,
        ForeignKeyConstraint foreignKey) throws SQLException, IOException {
        List<ForeignKeyMismatch> result = new ArrayList<ForeignKeyMismatch>();
        foreignKeyNotFound(database, tableModel, foreignKey, result::add);
        return result;
    }

    /**
     * Retrieve the Foreign Key Mismatches one by one (they are not kept in memory :
     * a massively invalid table may have millions of mismatches).
     *
     * @param database
     * @param tableModel
     * @param foreignKey
     * @param consumer   called for each mismatch, in the order of the query
     * @return the number of mismatches
     * @throws SQLException
     * @throws IOException
     */
    public int foreignKeyNotFound(
        Database database,
        TableModel tableModel,
        ForeignKeyConstraint foreignKey,
        Consumer<ForeignKeyMismatch> consumer) throws SQLException, IOException {

        // TODO validate condition to avoid SQL injection and crashes
        // grammar validation with ANTLR v4

        List<String> conditions = new ArrayList<String>();
        for (int i = 0; i < foreignKey.getSourceColumnNames().size(); i++) {
            String condition = "src." + foreignKey.getSourceColumnNames().get(i)
                + " = "
                + " target." + foreignKey.getTargetColumnNames().get(i);
            if (i == 0) {
                conditions.add("WHERE " + condition);
            } else {
                conditions.add("AND " + condition);
            }
        }

        List<String> nullableClause = new ArrayList<String>();
        for (int i = 0; i < foreignKey.getSourceColumnNames().size(); i++) {
            String name = foreignKey.getSourceColumnNames().get(i);
            AttributeType<?> attribute = tableModel.getFeatureType().getAttribute(name);
            if (attribute == null || attribute.getConstraints().isRequired()) {
                continue;
            }
            String condition = "AND " + name + " IS NOT NULL";
            nullableClause.add(condition);
        }

        String tableName = tableModel.getName();

        String query = "SELECT src.__id, src.__file, "
            + String.join(", ", foreignKey.getSourceColumnNames())
            + " FROM " + tableName + " AS src"
            + " WHERE NOT EXISTS ( "
            + "    SELECT true "
            + "    FROM " + foreignKey.getTargetTableName() + " AS target "
            + String.join(" ", conditions)
            + ") "
            + String.join(" ", nullableClause);

        try (RowIterator it = database.query(query)) {
            int count = 0;
            while (it.hasNext()) {
                String[] row = it.next();
                String values = "";
                if (row.length > 2) {
                    values = String.join(", ", Arrays.copyOfRange(row, 2, row.length));
                }
                consumer.accept(new ForeignKeyMismatch(row[0], row[1], values));
                count++;
            }
            return count;
        }
    }

}
