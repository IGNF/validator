package fr.ign.validator.dgpr.database;

import java.util.ArrayList;
import java.util.List;

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
     * Validators
     */
    private List<Validator<Database>> validators = new ArrayList<>();

    /**
     * Create database from document model and document files
     * 
     * @param context
     * @param document
     * @throws Exception
     */
    public ValidatableDatabase(Context context, Document document) throws Exception {
        log.info(MARKER, "Create ValidatableDatabase...");
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
        for (Validator<Database> validator : getValidators()) {
            validator.validate(context, database);
        }
    }

    /**
     * get list of database validators
     * 
     * @return
     */
    public List<Validator<Database>> getValidators() {
        return validators;
    }

    /**
     * push a database validator to current list
     * 
     * @param validator
     */
    public void addValidator(Validator<Database> validator) {
        validators.add(validator);
    }

}
