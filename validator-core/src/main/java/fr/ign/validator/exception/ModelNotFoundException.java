package fr.ign.validator.exception;

import java.net.URL;

public class ModelNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModelNotFoundException(String documentModelName) {
        super("Model '" + documentModelName + "' not found");
    }

    public ModelNotFoundException(URL modelUri) {
        super("Model '" + modelUri.toString() + "' not found");
    }

    public ModelNotFoundException(String documentModelName, Throwable e) {
        super("Model '" + documentModelName + "' not found", e);
    }

    public ModelNotFoundException(URL modelUri, Throwable e) {
        super("Model '" + modelUri.toString() + "' not found", e);
    }

}
