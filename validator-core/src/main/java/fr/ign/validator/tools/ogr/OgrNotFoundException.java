package fr.ign.validator.tools.ogr;

/**
 *
 * ogr2ogr is missing in the system
 *
 * @author MBorne
 *
 */
public class OgrNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OgrNotFoundException() {
        super("ogr2ogr not found in system");
    }

    public OgrNotFoundException(String fullVersion) {
        super("fail to read ogr2ogr version ('" + fullVersion + "')");
    }

}
