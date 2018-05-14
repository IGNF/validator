package fr.ign.validator.exception;

/**
 * 
 * Failure to read metadata file
 * 
 * @author MBorne
 *
 */
public class InvalidMetadataException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMetadataException(String message){
		super(message);
	}
	
	public InvalidMetadataException(String message, Throwable e){
		super(message,e);
	}

}
