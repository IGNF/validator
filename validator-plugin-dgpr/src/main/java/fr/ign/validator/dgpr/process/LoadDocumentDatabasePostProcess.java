package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.validation.database.InclusionValidator;
import fr.ign.validator.dgpr.validation.database.ScenarioValidator;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.dgpr.validation.database.RelationValidator;
import fr.ign.validator.dgpr.database.ValidatableDatabase;
import fr.ign.validator.dgpr.validation.database.GraphTopologyValidator;
import fr.ign.validator.dgpr.validation.database.IdentifierValidator;
import fr.ign.validator.ValidatorListener;

/**
 *
 */
public class LoadDocumentDatabasePostProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("LoadDocumentDatabasePostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        DocumentModel documentModel = document.getDocumentModel();

        /*
         * Add standard database validators.
         * 
         * TODO move to validator-core
         */
        documentModel.addDatabaseValidator(new IdentifierValidator());
        documentModel.addDatabaseValidator(new RelationValidator());

        /*
         * DGPR specific database validators.
         */
        documentModel.addDatabaseValidator(new ScenarioValidator());
        documentModel.addDatabaseValidator(new InclusionValidator());
        documentModel.addDatabaseValidator(new GraphTopologyValidator());

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {
    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        log.info(
            MARKER,
            "Load document database"
        );
        ValidatableDatabase database = new ValidatableDatabase(context, document);
        log.info(
            MARKER,
            "Validate document database"
        );

        database.validate(context);
    }

}
