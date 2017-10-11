package fr.ign.validator.exception;

public class InvalidMetadataException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMetadataException(String message){
		super(message);
	}
	
	public InvalidMetadataException(String message, Throwable e){
		super(message,e);
	}

}
