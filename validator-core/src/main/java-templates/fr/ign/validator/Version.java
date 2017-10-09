package fr.ign.validator;

/**
 * 
 * Validator version
 * 
 * @author MBorne
 *
 */
public final class Version {
    public static final String VERSION = "${project.version}";

    public static String getVersion() {
        return VERSION;
    }

}

