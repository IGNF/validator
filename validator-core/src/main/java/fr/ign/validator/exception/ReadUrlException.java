package fr.ign.validator.exception;

import java.io.IOException;
import java.net.URL;

/**
 * Thrown on failure to read a given URL
 *
 * @author MBorne
 *
 */
public class ReadUrlException extends IOException {

    private static final long serialVersionUID = 1L;

    public ReadUrlException(URL url, Throwable e) {
        super("Fail to read url " + url, e);
    }

}
