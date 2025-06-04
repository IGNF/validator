package fr.ign.validator.exception;

/**
 * Failure in geometry transform.
 *
 * @author MBorne
 *
 */
public class GeometryTransformException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GeometryTransformException(String message) {
        super(message);
    }

    public GeometryTransformException(String message, Throwable e) {
        super(message, e);
    }

}
