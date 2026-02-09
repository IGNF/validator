package fr.ign.validator.validation.attribute;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.tools.EnvelopeUtils;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

/**
 * Validate "reference" constraints using validation database.
 *
 * @author MBorne
 *
 */
public class AttributeReferenceValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("RelationValidator");

    /**
     * hardcoded limit to avoid huge report on massively invalid table
     */
    private static final int LIMIT_PER_ATTRIBUTE = 10;

    /**
     * Validate "reference" constraints for each attribute.
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
        log.info(MARKER, "Looking for attributes with reference constraints...");

        /*
         * Validate each attribute marked as unique
         */
        for (TableModel tableModel : ModelHelper.getTableModels(context.getDocumentModel())) {
            context.beginModel(tableModel);
            FeatureType featureType = tableModel.getFeatureType();
            for (AttributeType<?> attribute : featureType.getAttributes()) {
                String reference = attribute.getConstraints().getReference();
                if (StringUtils.isEmpty(reference)) {
                    continue;
                }

                context.beginModel(attribute);

                String sourceTableName = tableModel.getName();
                String sourceColumnName = attribute.getName();
                String targetTableName = attribute.getTableReference();
                String targetColumnName = attribute.getAttributeReference();

                /*
                 * Retrieve rows
                 */
                log.info(
                    MARKER, "Ensure that {}.{} references to {}.{} are valid...",
                    sourceTableName,
                    sourceColumnName,
                    targetTableName,
                    targetColumnName
                );
                String sql = getSqlFindInvalidRows(
                    sourceTableName, sourceColumnName, targetTableName, targetColumnName
                );
                RowIterator it = database.query(sql);

                /*
                 * Prepare reporting
                 */
                int count = 0;
                int indexValue = it.getColumn(sourceColumnName);
                AttributeType<?> attributeFeatureId = featureType.getIdentifier();
                int indexFeatureId = attributeFeatureId != null ? it.getColumn(attributeFeatureId.getName()) : -1;
                // TODO add support for different column name (kept for validator-plugin-dgpr)
                int indexGeom = it.getColumn("WKT");

                while (it.hasNext()) {
                    count++;
                    String[] row = it.next();

                    /*
                     * retrieve the id of the feature (kept for validator-plugin-dgpr)
                     */
                    String featureId = "";
                    if (indexFeatureId >= 0) {
                        featureId = row[indexFeatureId];
                    }

                    /*
                     * retrieve the bounding box of the feature (kept for validator-plugin-dgpr)
                     */
                    Envelope featureBoundingBox = null;
                    if (indexGeom >= 0 && !StringUtils.isEmpty(row[indexGeom])) {
                        featureBoundingBox = EnvelopeUtils.getEnvelope(row[indexGeom], context.getProjection());
                    }

                    context.report(
                        /*
                         * Note that scope DIRECTORY is mainly forced to ease integration in current
                         * client.
                         */
                        context.createError(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND)
                            .setScope(ErrorScope.DIRECTORY)
                            .setFileModel(tableModel.getName())
                            .setAttribute(attribute.getName())
                            .setFeatureId(featureId)
                            .setFeatureBbox(featureBoundingBox)
                            .setMessageParam("REF_VALUE", row[indexValue])
                            .setMessageParam("SOURCE_TABLE", sourceTableName)
                            .setMessageParam("SOURCE_COLUMN", sourceColumnName)
                            .setMessageParam("TARGET_TABLE", targetTableName)
                            .setMessageParam("TARGET_COLUMN", targetColumnName)
                    );

                }

                /*
                 * Report the number of errors for duplicated values
                 */
                log.info(
                    MARKER,
                    "Found {} invalid reference(s) from {}.{} to {}.{} (max : 10)",
                    count,
                    sourceTableName,
                    sourceColumnName,
                    targetTableName,
                    targetColumnName
                );

                it.close();
                context.endModel(attribute);
            }
            context.endModel(tableModel);
        }
    }

    /**
     * Get SQL statement to retrieve rows with invalid reference.
     *
     * @param sourceTableName
     * @param sourceColumnName
     * @param targetTableName
     * @param targetColumnName
     * @return
     */
    private String getSqlFindInvalidRows(
        String sourceTableName,
        String sourceColumnName,
        String targetTableName,
        String targetColumnName) {
        String sql = "SELECT s.* FROM " + sourceTableName + " s";
        sql += " WHERE NOT s." + sourceColumnName + " IN (SELECT " + targetColumnName + " FROM " + targetTableName
            + " t WHERE s." + sourceColumnName + " = t." + targetColumnName + ")";

        sql += " AND NOT s." + sourceColumnName + " IS NULL";
        sql += " LIMIT " + LIMIT_PER_ATTRIBUTE;
        return sql;
    }

}
