package fr.ign.validator.model.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.model.FileModel;

/**
 * Represents a file storing multiple tables. Note that it currently only
 * supports GML format as it is introduced to validate PCRS data. It might be
 * extended to support other formats such as GeoPackage.
 *
 * @author MBorne
 *
 */
public class MultiTableModel extends FileModel {
    public static final String TYPE = "multi_table";

    private static final String SUFFIXES_REGEXP = "\\.(gml|GML|gpkg|GPKG)";

    private List<EmbeddedTableModel> tableModels = new ArrayList<>();

    public MultiTableModel() {
        super();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getRegexpSuffix() {
        return SUFFIXES_REGEXP;
    }

    @Override
    public DocumentFile createDocumentFile(File path) {
        return new MultiTableFile(this, path);
    }

    public void setTableModels(List<EmbeddedTableModel> tableModels) {
        this.tableModels = tableModels;
    }

    @JsonProperty("tables")
    public List<EmbeddedTableModel> getTableModels() {
        return tableModels;
    }

    /**
     * Find table model by name.
     *
     * @param name
     * @return
     */
    public EmbeddedTableModel getTableModelByName(String name) {
        for (EmbeddedTableModel tableModel : tableModels) {
            if (tableModel.getName().equalsIgnoreCase(name)) {
                return tableModel;
            }
            if (tableModel.getPath() != null && name.matches("(?i)" + tableModel.getPath())) {
                return tableModel;
            }
        }
        return null;
    }

}
