package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.dgpr.validation.database.GraphTopologyValidator;
import fr.ign.validator.dgpr.validation.database.IdentifierValidator;
import fr.ign.validator.dgpr.validation.database.InclusionValidator;
import fr.ign.validator.dgpr.validation.database.RelationValidator;
import fr.ign.validator.dgpr.validation.database.ScenarioValidator;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.validation.Validator;

/**
 * Add DGPR specific database validators to the DocumentModel.
 */
public class CustomizeDatabaseValidation implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CustomizeDatabaseValidation");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        DocumentModel documentModel = document.getDocumentModel();

        /*
         * Add standard database validators.
         * 
         * TODO move to DocumentModel constructor in validator-core
         */
        documentModel.addDatabaseValidator(new IdentifierValidator());
        documentModel.addDatabaseValidator(new RelationValidator());

        /*
         * DGPR specific database validators.
         */
        log.info(MARKER, "Register custom database validator for DGPR plugin...");
        documentModel.addDatabaseValidator(new ScenarioValidator());
        documentModel.addDatabaseValidator(new InclusionValidator());
        documentModel.addDatabaseValidator(new GraphTopologyValidator());
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        log.info(MARKER, "Create validation Database...");
        Database database = Database.createDatabase(context, true);
        database.createTables(document.getDocumentModel());
        database.createIndexes(document.getDocumentModel());
        database.setProjection(context.getProjection());
        database.load(context, document);

        log.info(MARKER, "Validate document using database validators...");
        for (Validator<Database> validator : context.getDocumentModel().getDatabaseValidators()) {
            validator.validate(context, database);
        }
    }

}
