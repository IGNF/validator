package fr.ign.validator.mapping;

import java.io.File;

import fr.ign.validator.model.FileModel;

/**
 * Maps header with attributes of a FeatureType
 *
 * @author DDarras
 */
public class MisplacedFile {

    public enum Status {
        /**
         * Standard status
         */
        FILE_MISPLACED,
        /**
         * Multiple files for same FileModel
         */
        FILE_MODEL_OVERLOAD
    }

    /**
     * Input FileModel
     */
    private FileModel fileModel;

    /**
     * Input path
     */
    private File file;

    private MisplacedFile.Status status;

    /**
     * @param FileModel fileModel
     * @param File      path
     */
    public MisplacedFile(FileModel fileModel, File file) {
        this.fileModel = fileModel;
        this.file = file;
        this.status = MisplacedFile.Status.FILE_MISPLACED;
    }

    public FileModel getFileModel() {
        return fileModel;
    }

    public File getFile() {
        return file;
    }

    public MisplacedFile.Status getStatus() {
        return status;
    }

    public void setStatus(MisplacedFile.Status status) {
        this.status = status;
    }
}
