package fr.ign.validator.exception;

/**
 * 
 * Failure while reading error configuration
 * 
 * @author MBorne
 *
 */
public class InvalidErrorConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvalidErrorConfigException(String message) {
        super(message);
    }

    public InvalidErrorConfigException(String message, Throwable e) {
        super(message, e);
    }

}
