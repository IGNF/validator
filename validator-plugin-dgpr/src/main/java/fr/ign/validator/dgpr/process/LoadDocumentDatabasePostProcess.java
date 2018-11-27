package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.database.Database;
import fr.ign.validator.dgpr.validation.database.InclusionValidator;
import fr.ign.validator.dgpr.validation.database.MinMaxCoverageValidator;
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
		// load database
		Database database = Database.createDatabase(document);
		database.load(document);

		// validate inclusion
		InclusionValidator inclusionValidator = new InclusionValidator();
		inclusionValidator.validate(context, document, database);

		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();
		coverageValidator.validate(context, document, database);
	}

}
