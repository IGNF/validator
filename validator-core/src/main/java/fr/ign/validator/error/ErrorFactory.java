package fr.ign.validator.error;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.tools.CSVReader;

/**
 * 
 * Creates ValidatorError according to prototypes loaded from configuration files
 * 
 * @author MBorne
 * 
 */
public class ErrorFactory {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("ErrorFactory");

	/**
	 * ValidatorError prototypes with template messages
	 */
	private List<ValidatorError> prototypes = new ArrayList<ValidatorError>();

	public ErrorFactory() {
		loadDefaultErrors();
	}

	/**
	 * Gets loaded prototypes
	 * 
	 * @return
	 */
	public List<ValidatorError> getPrototypes() {
		return prototypes;
	}

	/**
	 * Creates a new error with its code and message parameters
	 * 
	 * @param code
	 * @return
	 */
	public ValidatorError newError(ErrorCode code, Object... args) {
		ValidatorError validatorError = findPrototype(code);
		if (null == validatorError) {
			throw new RuntimeException(String.format("L'erreur %1s n'est pas configurée", code.toString()));
		}

		try {
			ValidatorError result = (ValidatorError) validatorError.clone();
			try {
				result.setMessage(String.format(result.getMessage(), args));
			} catch (IllegalFormatException e) {
				// ignoring missing parameters
			}
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads prototypes from CSV file
	 * 
	 * @param csvFile
	 */
	private void loadCSV(Reader reader) throws IOException {
		CSVReader csvFile = new CSVReader(reader, StandardCharsets.UTF_8);
		String[] header = csvFile.next();
		if (header.length != 4) {
			throw new RuntimeException(String.format("L'entête du fichier est invalide (5 éléments sont attendus)"));
		}

		/*
		 * Loop on table lines
		 */
		while (csvFile.hasNext()) {
			String[] attributes = csvFile.next();
			if (attributes.length != 4) {
				continue;
			}
			// constructing new error
			log.trace(MARKER, "Chargement de l'erreur {},{},{}", attributes[0], attributes[1], attributes[2]);
			ErrorCode errorCode = ErrorCode.valueOf(attributes[0]);
			ErrorLevel errorLevel = ErrorLevel.valueOf(attributes[1]);
			String errorMessage = attributes[2];

			ValidatorError validatorError = new ValidatorError(errorCode);
			validatorError.setLevel(errorLevel);
			validatorError.setMessage(errorMessage);

			prototypes.add(validatorError);
		}
	}

	/**
	 * Finds prototype for the given code
	 * 
	 * @param code
	 * @return
	 */
	private ValidatorError findPrototype(ErrorCode code) {
		for (ValidatorError validatorError : prototypes) {
			if (validatorError.getCode().equals(code)) {
				return validatorError;
			}
		}
		return null;
	}

	/**
	 * Loads default error templates from
	 * validator-core/src/main/resources/validator-error-configuration.csv
	 */
	private void loadDefaultErrors() {
		try {
			// TODO split file
			Reader reader = new InputStreamReader(
					getClass().getResourceAsStream("/validator-error-configuration.csv"),
					StandardCharsets.UTF_8
			);
			loadCSV(reader);
		} catch (IOException e) {
			throw new RuntimeException("Fail to load validator-error-configuration.csv", e);
		}
	}

}
