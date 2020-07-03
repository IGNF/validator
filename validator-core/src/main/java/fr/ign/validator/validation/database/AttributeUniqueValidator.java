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
    public static final Marker MARKER = MarkerManager.getMarker("IdentifierValidator");

    /**
     * Context
     */
    private Context context;

    /**
     * Document
     */
    private Database database;

    /**
     * Check if there every ID is unique in a given table
     * 
     * @param context
     * @param document
     * @param database
     * @throws Exception
     */
    public void validate(Context context, Database database) {
        // context
        this.context = context;
        this.database = database;
        try {
            runValidation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void runValidation() throws SQLException, IOException {
        List<FileModel> fileModelsList = context.getDocumentModel().getFileModels();

        // For each table
        for (FileModel fileModel : fileModelsList) {
            if (!(fileModel instanceof TableModel)) {
                continue;
            }

            // TODO context.beginModel(fileModel)

            // Looking for attributes who are identifiers
            for (AttributeType<?> attribute : fileModel.getFeatureType().getAttributes()) {
                if (!attribute.getConstraints().isUnique()) {
                    continue;
                }
                // TODO context.beginModel(attribute)
                validateOneIdentifier(attribute.getName(), fileModel);
                // TODO context.endModel(attribute)
            }
            // TODO context.endModel(fileModel)
        }
    }

    private void validateOneIdentifier(String identifier, FileModel fileModel) throws SQLException, IOException {
        // TODO HAVING count(*) > 1 ORDER BY count(*) DESC
        // TODO LIMIT 10 ?
        RowIterator table = database.query(
            "SELECT " + identifier + " AS id, Count(" + identifier + ") AS count "
                + " FROM " + fileModel.getName()
                + " GROUP BY " + identifier
        );
        int indexId = table.getColumn("id");
        int indexCount = table.getColumn("count");

        while (table.hasNext()) {
            String[] row = table.next();

            int compte = Integer.parseInt(row[indexCount]);

            // if not unique, send error
            // remove condition by adding HAVING count(*) > 1
            if (compte > 1) {
                context.report(
                    context.createError(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE)
                        .setScope(ErrorScope.HEADER)
                        .setFileModel(fileModel.getName())
                        .setMessageParam("TABLE_NAME", fileModel.getName())
                        .setMessageParam("ID_NAME", row[indexId])
                        .setMessageParam("ID_COUNT", row[indexCount])
                );
            }

        }
        table.close();
    }

}
