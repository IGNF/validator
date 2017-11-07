package fr.ign.validator.exception;

/**
 * 
 * Fail to read a file according to a given charset
 * 
 * @author MBorne
 *
 */
public class InvalidCharsetException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidCharsetException(String message){
		super(message);
	}
	
}
