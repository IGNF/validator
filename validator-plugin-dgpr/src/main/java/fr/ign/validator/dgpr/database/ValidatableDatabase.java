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
 * @author sai
 */
public class ValidatableDatabase implements Validatable {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("ValidatableDatabase");

	/**
	 * Database
	 */
	private Database database;

	/**
	 * Document
	 */
	private Document document;

	/**
	 * Validators
	 */
	private List<Validator<Database>> validators = new ArrayList<>();


	/**
	 * Create database from document model and document files
	 * @param context
	 * @param document
	 * @throws Exception
	 */
	public ValidatableDatabase(Context context, Document document) throws Exception {
		this.document = document;
		this.database = Database.createDatabase(document);
		database.load(context, document);
	}


	@Override
	public void validate(Context context) throws Exception {
		log.info(MARKER, "Validation de la database {}",
			document.getDocumentModel().getName()
		);

		/*
		 * Validation at document level
		 */
		for (Validator<Database> validator : getValidators()) {
			validator.validate(context, database);
		}

	}


	/**
	 * get list of database validators
	 * @return
	 */
	public List<Validator<Database>> getValidators() {
		return validators;
	}


	/**
	 * set list of database validators
	 * @param validators
	 */
	public void setValidators(List<Validator<Database>> validators) {
		this.validators = validators;
	}


	/**
	 * push a database validator to current list
	 * @param validator
	 */
	public void addValidator(Validator<Database> validator) {
		validators.add(validator);
	}

}