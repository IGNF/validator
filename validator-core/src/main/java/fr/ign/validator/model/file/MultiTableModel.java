package fr.ign.validator.model.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

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
        return "\\.(gml|GML)";
    }

    @Override
    public DocumentFile createDocumentFile(File path) {
        return new MultiTableFile(this, path);
    }

    @XmlElementWrapper(name = "tables")
    @XmlElement(name = "table")
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
        }
        return null;
    }

    public void setTableModels(List<EmbeddedTableModel> tableModels) {
        this.tableModels = tableModels;
    }

}
