package fr.ign.validator.error;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.exception.InvalidErrorConfigException;

/**
 * 
 * Creates ValidatorError according to prototypes loaded from configuration
 * files
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
    private Map<ErrorCode, ValidatorError> prototypes = new LinkedHashMap<ErrorCode, ValidatorError>();

    public ErrorFactory() {
        loadDefaultErrors();
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
     * Gets loaded prototypes
     * 
     * @return
     */
    public Collection<ValidatorError> getPrototypes() {
        return prototypes.values();
    }

    /**
     * Finds prototype for the given code
     * 
     * @param code
     * @return
     */
    private ValidatorError findPrototype(ErrorCode code) {
        return prototypes.get(code);
    }

    /**
     * Add or replace a prototype
     * 
     * @param validatorError
     */
    private void addOrReplacePrototype(ValidatorError validatorError) {
        log.trace(MARKER, "Register template for code '{}' ...", validatorError.getCode());
        prototypes.put(validatorError.getCode(), validatorError);
    }

    /**
     * Load error codes from a given file.
     * 
     * @param errorConfigPath
     * @throws FileNotFoundException
     */
    public void loadErrorCodes(File errorConfigPath) {
        log.info(MARKER, "Load error codes from {} ...", errorConfigPath);
        try {
            InputStream is = new FileInputStream(errorConfigPath);
            loadErrorCodes(is);
        } catch (FileNotFoundException e) {
            String message = String.format("file '%1s' not found", errorConfigPath);
            throw new InvalidErrorConfigException(message, e);
        }
    }

    /**
     * Loads default error templates from
     * validator-core/src/main/resources/error-code.json
     */
    private void loadDefaultErrors() {
        log.trace(MARKER, "Loading errors from validator-core/src/main/resources/error-code.json ...");
        InputStream is = getClass().getResourceAsStream("/error-code.json");
        loadErrorCodes(is);
    }

    /**
     * Load error codes from an input stream.
     * 
     * @param is
     */
    private void loadErrorCodes(InputStream is) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<ValidatorError> validatorErrors = mapper.readValue(is, new TypeReference<List<ValidatorError>>() {
            });
            for (ValidatorError validatorError : validatorErrors) {
                addOrReplacePrototype(validatorError);
            }
        } catch (IOException e) {
            String message = String.format("Fail to read error templates");
            throw new InvalidErrorConfigException(message, e);
        }
    }

}
