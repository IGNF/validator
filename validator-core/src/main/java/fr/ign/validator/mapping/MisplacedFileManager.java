package fr.ign.validator.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.FileModel;


/**
 * Maps header with attributes of a FeatureType
 *
 * @author DDarras
 */
public class MisplacedFileManager {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("MisplacedFileManager");

    /**
     * FileModels / File associations
     */
    private List<MisplacedFile> misplacedFiles = new ArrayList<>();

    public MisplacedFileManager()
    {}

    /**
     * Adds a misplacedFile, checking if corresponding FileModel is overloaded
     *
     * @param fileModel
     * @param File
     */
    public void addMisplacedFile (FileModel fileModel, File File) {
        MisplacedFile misplacedFile = new MisplacedFile(fileModel, File);

        for (MisplacedFile otherFile : this.misplacedFiles) {
            if (otherFile.getFileModel().equals(fileModel)) {
                log.info(MARKER, "Found multiple files for model {} (FILE_MODEL_OVERLOAD)", fileModel);
                otherFile.setStatus(MisplacedFile.Status.FILE_MODEL_OVERLOAD);
                misplacedFile.setStatus(MisplacedFile.Status.FILE_MODEL_OVERLOAD);
                continue;
            }
        }
        this.misplacedFiles.add(misplacedFile);
    }

    public List<MisplacedFile> getMisplacedFiles() {
        return misplacedFiles;
    }
}
