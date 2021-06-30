package fr.ign.validator.cnig.sup.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import fr.ign.validator.cnig.sup.DatabaseSUP;

/**
 * Helper to locate CSV files in output directory.
 * 
 * @author MBorne
 *
 */
public class FileLocator {

    private File dataDirectory;

    public FileLocator(File dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public File findServitudeFile() {
        return findByName("SERVITUDE");
    }

    public File findActeSupFile() {
        return findByName("ACTE_SUP");
    }

    public File findServitudeActeSupFile() {
        return findByName("SERVITUDE_ACTE_SUP");
    }

    public List<File> findGenerateurSupFiles() {
        return findByRegex(DatabaseSUP.REGEX_TABLE_GENERATEUR);
    }

    public List<File> findAssietteSupFiles() {
        return findByRegex(DatabaseSUP.REGEX_TABLE_ASSIETTE);
    }

    /**
     * Helper - find a single CSV file
     * 
     * @param name
     * @return
     */
    File findByName(String name) {
        String[] extensions = {
            "csv", "CSV"
        };
        Collection<File> files = FileUtils.listFiles(dataDirectory, extensions, true);
        for (File file : files) {
            if (FilenameUtils.getBaseName(file.getName()).equals(name)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Helper - find multiple single CSV file
     * 
     * @param regexp
     * @return
     */
    List<File> findByRegex(String regexp) {
        List<File> results = new ArrayList<File>();

        String[] extensions = {
            "csv", "CSV"
        };
        Collection<File> files = FileUtils.listFiles(dataDirectory, extensions, true);

        for (File file : files) {
            if (!file.getName().matches(regexp)) {
                continue;
            }

            results.add(file);
        }
        return results;
    }

}
