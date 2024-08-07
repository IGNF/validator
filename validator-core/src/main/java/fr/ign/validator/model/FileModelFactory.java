package fr.ign.validator.model;

import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.SingleTableModel;

/**
 *
 * Factory providing FileModel creation by name (Table, PDF,etc.)
 *
 * @author MBorne
 *
 */
public class FileModelFactory {

    /**
     * Create a file model for a given type
     *
     * @param name
     * @return
     */
    public static FileModel createFileModelByType(String type) {
        if (DirectoryModel.TYPE.equals(type)) {
            return new DirectoryModel();
        } else if (SingleTableModel.TYPE.equals(type)) {
            return new SingleTableModel();
        } else if (MultiTableModel.TYPE.equals(type)) {
            return new MultiTableModel();
        } else if (PdfModel.TYPE.equals(type)) {
            return new PdfModel();
        } else if (MetadataModel.TYPE.equals(type)) {
            return new MetadataModel();
        } else {
            throw new IllegalArgumentException(String.format("invalid FileModel type : %s", type));
        }
    }

}
