package fr.ign.validator.error;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	public ValidatorError newError(ErrorCode code) {
		ValidatorError validatorError = findPrototype(code);
		if (null == validatorError) {
			throw new RuntimeException(String.format("L'erreur %1s n'est pas configurée", code.toString()));
		}
		try {
			ValidatorError result = (ValidatorError) validatorError.clone();
			return result;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
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
	 * Loads default error templates from validator-core/src/main/resources/error-code.json
	 * @throws IOException
	 */
	private void loadDefaultErrors() {
		try {
			InputStream is = getClass().getResourceAsStream("/error-code.json");
			ObjectMapper mapper = new ObjectMapper();
			this.prototypes = mapper.readValue(is, new TypeReference<List<ValidatorError>>(){});
		}catch(IOException e){
			throw new RuntimeException("Fail to load error-code.json", e);
		}
	}

}
