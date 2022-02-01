package fr.ign.validator.validation.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.internal.ForeignKeyConflictFinder;
import fr.ign.validator.database.internal.ForeignKeyConflictFinder.ForeignKeyConflict;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.FeatureTypeConstraints;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.constraint.ForeignKeyConstraint;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

public class ForeignKeyValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("ForeignKeyValidator");

    private ForeignKeyConflictFinder conflictFinder = new ForeignKeyConflictFinder();

    /**
     * Check if every conditions is respected
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

        log.info(MARKER, "Looking for FeatureType with foreignKeys constraints...");

        /*
         * Validate each FeatureType who have conditions
         */
        for (TableModel tableModel : ModelHelper.getTableModels(context.getDocumentModel())) {
            context.beginModel(tableModel);
            FeatureTypeConstraints constraints = tableModel.getFeatureType().getConstraints();
            for (ForeignKeyConstraint foreignKey : constraints.getForeignKeys()) {
                log.info(
                    MARKER, "Table {} : checking '{}' foreignKey.",
                    tableModel.getName(), foreignKey
                );

                List<ForeignKeyConflict> conflicts = conflictFinder.findForeignKeyConflict(
                    database,
                    tableModel.getName(),
                    foreignKey
                );

                /*
                 * report errors
                 */
                log.info(
                    MARKER,
                    "Table {} : foreignKey {} found {} conflicts (max : {})",
                    tableModel.getName(),
                    foreignKey,
                    conflicts.size(),
                    ForeignKeyConflictFinder.LIMIT_ERROR_COUNT
                );
                for (ForeignKeyConflict conflict : conflicts) {
                    context.report(
                        context.createError(CoreErrorCodes.DATABASE_FOREIGN_KEY_CONFLICT)
                            .setScope(ErrorScope.FEATURE)
                            .setFile(conflict.file)
                            .setFileModel(tableModel.getName())
                            .setAttribute("--")
                            .setId(conflict.id)
                            .setMessageParam("CONDITION", foreignKey.toString())
                    );
                }
            }
            context.endModel(tableModel);

        }
    }

}
