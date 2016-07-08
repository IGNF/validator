package fr.ign.validator.error;

import java.io.File;
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
import fr.ign.validator.tools.CharsetDetector;

/**
 * 
 * Fabrique pour les erreurs du validateur
 * 
 * @author MBorne
 * 
 */
public class ErrorFactory {
	/**
	 * Instance de log4j
	 */
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("ErrorFactory");

	/**
	 * La liste des erreurs servant de modèle pour la constructions des erreurs
	 * de validation.
	 */
	private List<ValidatorError> prototypes = new ArrayList<ValidatorError>();

	/**
	 * Constructeur par défaut
	 */
	private ErrorFactory() {

	}

	/**
	 * Renvoie les modèles d'erreur
	 * 
	 * @return
	 */
	public List<ValidatorError> getPrototypes() {
		return prototypes;
	}

	/**
	 * Définit la liste des modèles d'erreurs
	 * 
	 * @param prototypes
	 */
	public void setPrototypes(List<ValidatorError> prototypes) {
		this.prototypes = prototypes;
	}

	/**
	 * Construction d'une nouvelle erreur à partir d'un code d'erreur
	 * 
	 * @param code
	 * @return
	 */
	public ValidatorError newError(ErrorCode code, Object... args) {
		ValidatorError validatorError = findPrototype(code);
		if (null == validatorError) {
			throw new RuntimeException(String.format(
					"L'erreur %1s n'est pas configurée", code.toString()));
		}

		try {
			ValidatorError result = (ValidatorError) validatorError.clone();
			try {
				result.setMessage(String.format(result.getMessage(), args));
			} catch (IllegalFormatException e) {
				// on ignore les paramètres manquants
			}
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Recherche d'un prototype
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
	 * Charge le fichier CSV de configuration des erreurs
	 * 
	 * @param file
	 * @throws IOException
	 */
	public void loadCSV(File file) throws IOException {
		this.prototypes.clear();

		CSVReader csvFile = new CSVReader(file,CharsetDetector.detectCharset(file));
		loadCSV(csvFile) ;
	}


	public void loadCSV(Reader reader) throws IOException {
		this.prototypes.clear();

		CSVReader csvFile = new CSVReader(reader,StandardCharsets.UTF_8);
		loadCSV(csvFile) ;
	}

	
	
	private void loadCSV(CSVReader csvFile){
		String[] header = csvFile.next();
		if (header.length != 5) {
			throw new RuntimeException(String.format("L'entête du fichier est invalide (5 éléments sont attendus)"));
		}

		/*
		 * Boucle sur les lignes de la table
		 */
		while (csvFile.hasNext()) {
			String[] attributes = csvFile.next();
			if ( attributes.length != 5 ){
				continue ;
			}
			// on construit la nouvelle erreur
			log.trace(MARKER, "Chargement de l'erreur {},{},{}", attributes[0], attributes[1], attributes[2]);
			ErrorCode errorCode = ErrorCode.valueOf(attributes[0]);
			ErrorScope errorContext = ErrorScope.valueOf(attributes[1]);
			ErrorLevel errorLevel = ErrorLevel.valueOf(attributes[2]);
			String errorMessage = attributes[3];

			ValidatorError validatorError = new ValidatorError(errorCode);
			validatorError.setScope(errorContext);
			validatorError.setLevel(errorLevel);
			validatorError.setMessage(errorMessage);

			prototypes.add(validatorError);
		}
	}
	

	/**
	 * Construction d'une instance à partir de la ressource
	 * validator-error-configuration.csv
	 * 
	 * @return
	 */
	public static ErrorFactory newFromRessource() {
		Reader reader = new InputStreamReader(
			ErrorFactory.class.getResourceAsStream("/validator-error-configuration.csv"),
			StandardCharsets.UTF_8
		) ;
		try {
			ErrorFactory errorFactory = new ErrorFactory();
			errorFactory.loadCSV(reader);
			return errorFactory;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Construction d'une instance vide (test & extension)
	 * 
	 * @return
	 */
	public static ErrorFactory newEmptyInstance() {
		return new ErrorFactory();
	}

}
