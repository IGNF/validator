package fr.ign.validator.model.file;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.Model;

/**
 * A table in a {@link MultiTableModel}
 * 
 * @author MBorne
 *
 */
public class EmbeddedTableModel implements Model {

    /**
     * Nom of the table.
     */
    private String name;

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

    @XmlTransient
    public void setName(String name) {
        this.name = name;
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
