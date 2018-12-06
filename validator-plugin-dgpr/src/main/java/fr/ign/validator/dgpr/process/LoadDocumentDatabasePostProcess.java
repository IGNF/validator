package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.validation.database.InclusionValidator;
import fr.ign.validator.dgpr.validation.database.MinMaxCoverageValidator;
import fr.ign.validator.dgpr.validation.database.ScenarioValidator;
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
	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {
		log.info(MARKER,
			"Load document database"
		);

		ValidatableDatabase database = new ValidatableDatabase(context, document);

		log.info(MARKER,
			"Validate document database"
		);

		/*
		 * Standard database Validation 
		 */
		database.addValidator(new IdentifierValidator());
		database.addValidator(new RelationValidator());

		/*
		 * Custom database Validation
		 */
		database.addValidator(new ScenarioValidator());
		database.addValidator(new MinMaxCoverageValidator());
		database.addValidator(new InclusionValidator());
		database.addValidator(new GraphTopologyValidator());
		
		database.validate(context);
	}

}
