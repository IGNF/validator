package fr.ign.validator.exception;

/**
 * 
 * Failure while reading models
 * 
 * @author MBorne
 *
 */
public class InvalidModelException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidModelException(String message) {
        super(message);
    }

    public InvalidModelException(String message, Throwable e) {
        super(message, e);
    }

}
