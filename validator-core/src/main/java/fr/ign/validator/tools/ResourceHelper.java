package fr.ign.validator.tools;

import java.io.File;

public class ResourceHelper {

    /**
     * Get resource File corresponding to a given path in clazz package
     * 
     * @param path ex : "/config/cnig_PLU_2014/files.xml"
     * @return
     */
    public static File getResourceFile(Class<?> clazz, String path) {
        try {
            return new File(clazz.getResource(path).getPath());
        } catch (NullPointerException e) {
            throw new RuntimeException(
                "Resource '" + path + "' not found"
            );
        }
    }

}
