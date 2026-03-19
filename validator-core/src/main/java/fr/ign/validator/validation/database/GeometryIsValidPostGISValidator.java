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
import fr.ign.validator.database.internal.InvalidGeometryFinder;
import fr.ign.validator.database.internal.InvalidGeometryFinder.InvalidGeometry;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

/**
 *
 * Validates a geometry
 *
 * @author MBorne
 *
 */
public class GeometryIsValidPostGISValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GeometryIsValidPostGISValidator");

    private InvalidGeometryFinder invalidGeometryFinder = new InvalidGeometryFinder();

    @Override
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

    /**
     * @param context
     * @param database
     * @throws SQLException
     * @throws IOException
     */
    private void doValidate(Context context, Database database) throws SQLException, IOException {
        if (!context.isPostGISValidation()) {
            return;
        }
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            return;
        }

        log.info(MARKER, "Looking for geometry attributes...");

        /*
         * Validate each attribute with geometry Type
         */
        for (TableModel tableModel : ModelHelper.getTableModels(context.getDocumentModel())) {
            context.beginModel(tableModel);

            for (AttributeType<?> attribute : tableModel.getFeatureType().getAttributes()) {
                if (!attribute.isGeometry()) {
                    continue;
                }

                context.beginModel(attribute);

                AttributeType<?> identifier = tableModel.getFeatureType().getIdentifier();
                List<InvalidGeometry> invalidGeometries = new ArrayList<>();
                /*
                 * Retrieve duplicated values
                 */
                log.info(MARKER, "Table {}.{} : Validating Geometry...", tableModel.getName(), attribute.getName());
                if (identifier == null) {
                    invalidGeometries = invalidGeometryFinder.findInvalidGeometries(database, tableModel.getName(),
                            attribute.getName());
                } else {
                    invalidGeometries = invalidGeometryFinder.findInvalidGeometries(database, tableModel.getName(),
                            attribute.getName(), identifier.getName());
                }
                /*
                 * Report errors for duplicated values
                 */
                log.info(MARKER, "Table {}.{} : Found {} invalid geometry(ies) (max : {})", tableModel.getName(),
                        attribute.getName(), invalidGeometries.size(), InvalidGeometryFinder.LIMIT_PER_ATTRIBUTE);
                for (InvalidGeometry invalidGeometry : invalidGeometries) {
                    context.report(context.createError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID)
                            .setMessageParam("TYPE_ERROR", invalidGeometry.type).setFileModel(tableModel.getName())
                            .setAttribute(attribute.getName()).setId(invalidGeometry.id));
                }

                context.endModel(attribute);
            }

            context.endModel(tableModel);
        }
    }
}
