package fr.ign.validator.tools;

import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.SingleTableModel;

/**
 * Helpers to manipulate models.
 *
 * @author MBorne
 *
 */
public class ModelHelper {

    public ModelHelper() {
        // disabled (class with static helper)
    }

    /**
     * Traverse {@link DocumentModel} to retrieve all {@link SingleTableModel} and
     * {@link EmbeddedTableModel}
     *
     * @param documentModel
     * @return
     */
    public static List<TableModel> getTableModels(DocumentModel documentModel) {
        List<TableModel> result = new ArrayList<>();
        for (FileModel fileModel : documentModel.getFileModels()) {
            if (fileModel instanceof SingleTableModel) {
                result.add((TableModel) fileModel);
            } else if (fileModel instanceof MultiTableModel) {
                getTableModels((MultiTableModel) fileModel, result);
            }
        }
        return result;
    }

    private static void getTableModels(MultiTableModel fileModel, List<TableModel> result) {
        for (TableModel tableModel : fileModel.getTableModels()) {
            result.add(tableModel);
        }
    }

}
