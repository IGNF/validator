package fr.ign.validator.model.file;

import java.io.File;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.SingleTableFile;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FeatureTypeRef;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;

/**
 * A table stored in a file and associated to a FeatureType.
 * 
 * @author MBorne
 *
 */
public class SingleTableModel extends FileModel implements TableModel {
    public static final String TYPE = "table";

    private static final String SUFFIXES_REGEXP = "\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV|gpkg|GPKG)";

    /**
     * FeatureType reference.
     */
    private FeatureTypeRef featureTypeRef;

    /**
     * Table model (optional, for tables only)
     */
    private FeatureType featureType = null;

    public SingleTableModel() {
        super();
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @JsonProperty("tableModel")
    @JsonInclude(value = Include.NON_NULL)
    public FeatureTypeRef getFeatureTypeRef() {
        return featureTypeRef;
    }

    public void setFeatureTypeRef(FeatureTypeRef featureTypeRef) {
        this.featureTypeRef = featureTypeRef;
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
        return SUFFIXES_REGEXP;
    }

    @Override
    public DocumentFile createDocumentFile(File path) {
        return new SingleTableFile(this, path);
    }

}
