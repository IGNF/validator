package fr.ign.validator.model.file;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FeatureTypeRef;
import fr.ign.validator.model.TableModel;

/**
 * A table in a {@link MultiTableModel}
 *
 * @author MBorne
 *
 */
public class EmbeddedTableModel implements TableModel {

    /**
     * Nom of the table.
     */
    private String name;

    /**
     * Path of the table.
     */
    private String path;

    /**
     * FeatureType reference.
     */
    private FeatureTypeRef featureTypeRef;

    /**
     * Table model
     */
    private FeatureType featureType;

    public EmbeddedTableModel() {
        // nothing to initialiaze
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    @Override
    public String toString() {
        return name + " (" + getClass().getSimpleName() + ")";
    }

}
