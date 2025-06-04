package fr.ign.validator.io;

import java.net.URL;

import fr.ign.validator.exception.InvalidModelException;

/**
 * Create ModelReader instances
 *
 * @author MBorne
 */
public class ModelReaderFactory {

    private ModelReaderFactory() {
        // disabled (class with static helpers)
    }

    /**
     * Create a ModelReader detecting format for a given URL :
     *
     * <ul>
     * <li>Deprecated XmlModelReader only if URL ends with .xml</li>
     * <li>JsonModelReader for other cases</li>
     * </ul>
     *
     * @param url
     * @return
     */
    public static ModelReader createModelReader(URL url) {
        if (url.toString().endsWith(".xml")) {
            throw new InvalidModelException(
                "Fail to load " + url + " (XML model support has been removed, use JSON format)"
            );
        } else {
            return new JsonModelReader();
        }
    }

}
