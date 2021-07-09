package fr.ign.validator.data.file;

import java.io.File;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.model.file.MultiTableModel;

public class MultiTableFile extends DocumentFile {

    private MultiTableModel fileModel;

    public MultiTableFile(MultiTableModel fileModel, File path) {
        super(path);
        this.fileModel = fileModel;
    }

    @Override
    public MultiTableModel getFileModel() {
        return fileModel;
    }

    /**
     * TODO complete to validate according to {@link MultiTableModel}
     */
    @Override
    protected void validateContent(Context context) {

    }

}
