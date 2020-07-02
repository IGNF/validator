package fr.ign.validator.dgpr.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * 
 * @author CBouche
 */
public class ValidatableDatabase implements Validatable {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("ValidatableDatabase");

    /**
     * Database
     */
    private Database database;

    /**
     * Create database from document model and document files
     * 
     * @param context
     * @param document
     * @throws Exception
     */
    public ValidatableDatabase(Context context, Document document) throws Exception {
        log.info(MARKER, "Create validation Database...");
        this.database = Database.createDatabase(context, true);
        this.database.createTables(document.getDocumentModel());
        this.database.createIndexes(document.getDocumentModel());
        database.setProjection(context.getProjection());
        database.load(context, document);
    }

    @Override
    public void validate(Context context) throws Exception {
        log.info(MARKER, "Validate using database validators...");

        /*
         * Validation at document level
         */
        for (Validator<Database> validator : context.getDocumentModel().getDatabaseValidators()) {
            validator.validate(context, database);
        }
    }

}
