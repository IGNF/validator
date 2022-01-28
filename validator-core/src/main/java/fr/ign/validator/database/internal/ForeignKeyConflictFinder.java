package fr.ign.validator.database.internal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.model.constraint.ForeignKeyConstraint;

public class ForeignKeyConflictFinder {
	
	public static final Integer LIMIT_ERROR_COUNT = 10;


	public class ForeignKeyConflict {
		public String file;
		public String id;

        public ForeignKeyConflict(String id, String file) {
            this.id = id;
            this.file = file;
        }
	}


	public List<ForeignKeyConflict> findForeignKeyConflict(
			Database database,
			String tableName,
			ForeignKeyConstraint foreignKey
	) throws SQLException, IOException {

        // TODO validate condition to avoid SQL injection and crashes
        // grammar validation with ANTLR v4

		List<String> conditions = new ArrayList<String>();
		for (int i = 0; i < foreignKey.getSourceColumnNames().size(); i++) {
			String condition = "a." + foreignKey.getSourceColumnNames().get(i)
					+ " LIKE "
					+ "b." + foreignKey.getTargetColumnNames().get(i);
			conditions.add(condition);
		}
		String query = "SELECT r.__id, r.__file"
		      + " FROM " + tableName + " AS r"
		      + " WHERE r.__id NOT IN ("
			  + " SELECT a.__id "
		      + " FROM " + tableName + " AS a"
		      + " JOIN " + foreignKey.getTargetTableName() + " AS b"
		      + " ON (" + String.join(" AND ", conditions) + ")"
			  + " )";

        RowIterator it = database.query(query);

		List<ForeignKeyConflict> result = new ArrayList<ForeignKeyConflict>();
        while (it.hasNext()) {
            String[] row = it.next();
            result.add(new ForeignKeyConflict(row[0], row[1]));
        }
        it.close();
        return result;
	}

}
