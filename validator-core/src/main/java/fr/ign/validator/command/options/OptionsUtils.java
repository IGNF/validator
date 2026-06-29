package fr.ign.validator.command.options;

import java.io.File;

public class OptionsUtils {
    /**
     * Checks if child is a subdirectory of parent
     *
     * @param parent
     * @param child
     * @return
     */
    public static boolean isParentOf(File parent, File child) {
        if (child.getAbsolutePath().equals(parent.getAbsolutePath())) {
            return true;
        }
        while (child.getParentFile() != null) {

            child = child.getParentFile();
            if (child.getAbsolutePath().equals(parent.getAbsolutePath())) {

                return true;
            }
        }
        return false;
    }
}
