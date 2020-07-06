package fr.ign.validator.validation.database;

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
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.EnvelopeUtils;
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
     * Validate "reference" constraints for each attribute.
     */
    public void validate(Context context, Database database) {
        try {
            doValidate(context, database);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void doValidate(Context context, Database database) throws SQLException, IOException {
        log.info(MARKER, "Looking for attributes with reference constraints...");

        /*
         * Validate each attribute marked as unique
         */
        for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof TableModel)) {
                continue;
            }

            context.beginModel(fileModel);
            FeatureType featureType = fileModel.getFeatureType();
            for (AttributeType<?> attribute : featureType.getAttributes()) {
                String reference = attribute.getConstraints().getReference();
                if (StringUtils.isEmpty(reference)) {
                    continue;
                }

                context.beginModel(attribute);

                String sourceTableName = fileModel.getName();
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
                RowIterator table = database.query(sql);

                int count = 0;
                int indexValue = table.getColumn(sourceColumnName);
                // TODO add support for different column name (kept for validator-plugin-dgpr)
                int indexGeom = table.getColumn("WKT");

                while (table.hasNext()) {
                    count++;
                    String[] row = table.next();

                    Envelope envelope = null;
                    if (indexGeom >= 0 && !StringUtils.isEmpty(row[indexGeom])) {
                        envelope = EnvelopeUtils.getEnvelope(row[indexGeom], context.getProjection());
                    }

                    context.report(
                        /*
                         * Note that scope DIRECTORY is mainly forced to ease integration in current
                         * client.
                         */
                        context.createError(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND)
                            .setScope(ErrorScope.DIRECTORY)
                            .setFileModel(fileModel.getName())
                            .setAttribute(attribute.getName())
                            // TODO retrieve ID
                            // .setFeatureId(idObject)
                            .setFeatureBbox(envelope)
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

                table.close();
                context.endModel(attribute);
            }
            context.endModel(fileModel);
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
        sql += " LIMIT 10";
        return sql;
    }

}
