package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.database.DocumentDatabase;
import fr.ign.validator.dgpr.validation.database.InclusionDatabaseValidator;
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
		DocumentDatabase database = new DocumentDatabase(document);
		database.load();

		// validate inclusion
		InclusionDatabaseValidator validator = new InclusionDatabaseValidator();
		validator.validate(context, document, database);

		// database.addValidator(new InclusionDatabaseValidator());
		// database.validate();
	}

}
