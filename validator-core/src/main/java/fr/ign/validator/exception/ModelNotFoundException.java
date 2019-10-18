package fr.ign.validator.exception;

public class ModelNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ModelNotFoundException(String documentModelName) {
		super("Model '"+documentModelName+"' not found");
	}
	
}
