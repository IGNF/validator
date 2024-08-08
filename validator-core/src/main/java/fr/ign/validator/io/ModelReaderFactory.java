package fr.ign.validator.io;

import java.net.URL;

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
            return new XmlModelReader();
        } else {
            return new JsonModelReader();
        }
    }

}
