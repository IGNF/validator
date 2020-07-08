package fr.ign.validator.process;

import java.io.File;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Remove temp files from previous run before running validator.
 * 
 * @author MBorne
 *
 */
public class RemovePreviousFilesPreProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("RemovePreviousFilesPreProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        log.info(MARKER, "Remove files from previous execution...");
        String[] extensions = new String[] {
            TableReader.TMP_EXTENSION
        };
        Collection<File> tempFiles = FileUtils.listFiles(document.getDocumentPath(), extensions, true);
        for (File tempFile : tempFiles) {
            log.info(MARKER, "Remove file {}...", tempFile.getAbsolutePath());
            tempFile.delete();
        }
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {

    }

}
