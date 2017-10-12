package fr.ign.validator.cnig.exception;

/**
 * Naming convension is invalid
 * @author MBorne
 *
 */
public class InvalidDocumentNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidDocumentNameException(String message){
		super(message);
	}
	
}
