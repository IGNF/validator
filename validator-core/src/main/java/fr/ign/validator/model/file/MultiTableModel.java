package fr.ign.validator.model.file;

import java.io.File;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.model.FileModel;

/**
 * Represents a file storing multiple table data. Note that it currently only
 * supports GML format as it is introduced to validate PCRS data.
 * 
 * @author MBorne
 *
 */
public class MultiTableModel extends FileModel {
    public static final String TYPE = "multi_table";

    public MultiTableModel() {
        super();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getRegexpSuffix() {
        return "\\.(gml|GML)";
    }

    @Override
    public DocumentFile createDocumentFile(File path) {
        return new MultiTableFile(this, path);
    }

}
