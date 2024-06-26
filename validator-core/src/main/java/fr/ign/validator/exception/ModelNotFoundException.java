package fr.ign.validator.exception;

import java.net.URL;

public class ModelNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ModelNotFoundException(URL modelUri, Throwable e) {
        super("Model '" + modelUri.toString() + "' not found", e);
    }

}
