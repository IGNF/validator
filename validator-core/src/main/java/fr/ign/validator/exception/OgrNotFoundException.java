package fr.ign.validator.exception;

public class OgrNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OgrNotFoundException(){
		super("ogr2ogr not found in system");
	}
	
}
