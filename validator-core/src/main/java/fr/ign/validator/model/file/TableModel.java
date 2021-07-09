package fr.ign.validator.model.file;

import java.io.File;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;

/**
 * Represents a table associated to a FeatureType
 * 
 * @author MBorne
 *
 */
public class TableModel extends FileModel {
    public static final String TYPE = "table";

    /**
     * Table model (optional, for tables only)
     */
    private FeatureType featureType = null;

    public TableModel() {
        super();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonIgnore
    public FeatureType getFeatureType() {
        return featureType;
    }

    @XmlTransient
    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @Override
    public String getRegexpSuffix() {
        return "\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV)";
    }

    @Override
    public DocumentFile createDocumentFile(File path) {
        return new TableFile(this, path);
    }

}
