package fr.ign.validator.exception;

import java.io.IOException;

/**
 * Failure to find column by name.
 * 
 * @author MBorne
 *
 */
public class ColumnNotFoundException extends IOException {

    private static final long serialVersionUID = 1L;

    public ColumnNotFoundException(String columnName) {
        super("Column '" + columnName + "' not found");
    }

}
