package fr.ign.validator.exception;

/**
 * Unrecoverable runtime error (runtime issue, coding problem,...)
 * 
 * @author MBorne
 *
 */
public class ValidatorFatalError extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidatorFatalError(String message) {
        super(message);
    }

    public ValidatorFatalError(String message, Throwable e) {
        super(message, e);
    }

}
