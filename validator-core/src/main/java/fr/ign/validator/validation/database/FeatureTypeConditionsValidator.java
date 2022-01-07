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
import fr.ign.validator.database.internal.ConditionMismatchFinder;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.FeatureTypeConstraints;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

/**
 * FeatureType Condition Validator
 * - Database Validator
 * - Search FeatureType which has conditions constraints
 * - Perform ConditionMismatchFinder method on each conditions
 * @author cbouche
 *
 */
public class FeatureTypeConditionsValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("FeatureTypeConditionsValidator");

    private ConditionMismatchFinder conditionMismatchFinder = new ConditionMismatchFinder();

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
		log.info(MARKER, "Looking for FeatureType with conditions constraints...");

        /*
         * Validate each FeatureType who have conditions
         */
        for (TableModel tableModel : ModelHelper.getTableModels(context.getDocumentModel())) {
            context.beginModel(tableModel);
            FeatureTypeConstraints constraints = tableModel.getFeatureType().getConstraints();
            for(String condition : constraints.getConditions()) {
            	
                log.info(
                    MARKER, "Table {} : checking {} condition...",
                    tableModel.getName(),
                    condition
                );
                
                List<String> mismatchs = conditionMismatchFinder.findConditionMismatch(
                		database,
                		tableModel.getName(),
                		condition
        		);
                
                /*
                 * report errors
                 */
                log.info(
                    MARKER,
                    "Table {}, condition {} : Found {} mismatch (max : {})",
                    tableModel.getName(),
                    condition,
                    mismatchs.size(),
                    ConditionMismatchFinder.LIMIT_ERROR_COUNT
                );
                for (String tuppleReference : mismatchs) {
                    context.report(
                        context.createError(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE)
                            .setScope(ErrorScope.DIRECTORY)
                            .setFileModel(tableModel.getName())
                            .setMessageParam("TABLE_NAME", tableModel.getName())
                            .setMessageParam("CONDITION", condition)
                            .setMessageParam("REFERENCE", tuppleReference)
                    );
                }
            }
            context.endModel(tableModel);
            
        }
	}
	
}
