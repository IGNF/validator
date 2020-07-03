package fr.ign.validator.validation.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.validation.Validator;

/**
 * Validate attributes according to "unique" constraints.
 * 
 * @author CBouche
 * @author MBorne
 */
public class AttributeUniqueValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("AttributeUniqueValidator");

    /**
     * A duplicated value with the number of occurence
     * 
     * @author MBorne
     */
    private class DuplicatedValue {
        public String value;
        public int count;

        public DuplicatedValue(String value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    /**
     * Check if there every ID is unique in a given table
     * 
     * @param context
     * @param document
     * @param database
     * @throws Exception
     */
    public void validate(Context context, Database database) {
        try {
            doValidate(context, database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doValidate(Context context, Database database) throws SQLException, IOException {
        log.info(MARKER, "Looking for attributes with unique constraints...");

        /*
         * Validate each attribute marked as unique
         */
        for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof TableModel)) {
                continue;
            }

            context.beginModel(fileModel);
            for (AttributeType<?> attribute : fileModel.getFeatureType().getAttributes()) {
                if (!attribute.getConstraints().isUnique()) {
                    continue;
                }

                context.beginModel(attribute);

                /*
                 * Retrieve duplicated values
                 */
                log.info(
                    MARKER, "Table {}.{} : Looking for duplicated values...",
                    fileModel.getName(),
                    attribute.getName()
                );
                List<DuplicatedValue> duplicatedValues = findDuplicatedValues(
                    database,
                    fileModel.getName(),
                    attribute.getName()
                );

                /*
                 * Report errors for duplicated values
                 */
                log.info(
                    MARKER,
                    "Table {}.{} : Found {} duplicated value(s) (max : 10)",
                    fileModel.getName(),
                    attribute.getName(),
                    duplicatedValues.size()
                );
                for (DuplicatedValue duplicatedValue : duplicatedValues) {
                    context.report(
                        /*
                         * Note that scope DIRECTORY is mainly forced to ease integration in current
                         * client.
                         */
                        context.createError(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE)
                            .setScope(ErrorScope.DIRECTORY)
                            .setFileModel(fileModel.getName())
                            .setMessageParam("TABLE_NAME", fileModel.getName())
                            .setMessageParam("COLUMN_NAME", attribute.getName())
                            .setMessageParam("ID_NAME", duplicatedValue.value)
                            .setMessageParam("ID_COUNT", "" + duplicatedValue.count)
                    );
                }

                context.endModel(attribute);
            }
            context.endModel(fileModel);
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
    private List<DuplicatedValue> findDuplicatedValues(Database database, String tableName, String columnName)
        throws SQLException, IOException {
        RowIterator it = database.query(
            "SELECT " + columnName + " AS id, count(*) AS count"
                + " FROM " + tableName
                + " GROUP BY " + columnName
                + " HAVING count(*) > 1 ORDER BY count(*) DESC"
                + " LIMIT 10"
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
