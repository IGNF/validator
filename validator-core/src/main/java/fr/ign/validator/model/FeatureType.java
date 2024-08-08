package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import fr.ign.validator.model.type.GeometryType;

/**
 * Describe the content of a table
 *
 * @author MBorne
 */
public class FeatureType implements Model {
    /**
     * Parent (optional)
     */
    private FeatureType parent;
    /**
     * Type name
     */
    private String name;
    /**
     * Description
     */
    private String description;
    /**
     * Attribute list
     */
    private List<AttributeType<?>> attributes = new ArrayList<AttributeType<?>>();

    /**
     * Feature Type Constraints: foreign key and sql conditions
     */
    private FeatureTypeConstraints constraints = new FeatureTypeConstraints();

    public FeatureType() {

    }

    /**
     * Indicates if the FeatureType has a parent
     *
     * @return
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Gets parent of the FeatureType
     *
     * @return
     */
    public FeatureType getParent() {
        return parent;
    }

    /**
     * Defines the parent of the FeatureType
     *
     * @param featureType
     */
    public void setParent(FeatureType parent) {
        this.parent = parent;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String typeName) {
        this.name = typeName;
    }

    /**
     * Indicates if table is spatial
     *
     * @return
     */
    public boolean isSpatial() {
        for (int i = 0; i < getAttributeCount(); i++) {
            if (getAttribute(i).isGeometry()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get default geometry (the first one if multiple geometry are defined to match
     * GeoServer behavior).
     *
     * @return
     */
    public GeometryType getDefaultGeometry() {
        for (AttributeType<?> attribute : attributes) {
            if (attribute.isGeometry()) {
                return (GeometryType) attribute;
            }
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * True if no {@link FeatureType} is empty.
     */
    public boolean isEmpty() {
        return getAttributeCount() == 0;
    }

    public List<AttributeType<?>> getAttributes() {
        return attributes;
    }

    @JsonProperty("columns")
    public void setAttributes(List<AttributeType<?>> attributes) {
        this.attributes = attributes;
    }

    /**
     * Adds an attribute
     *
     * @param attribute
     */
    public void addAttribute(AttributeType<?> attribute) {
        this.attributes.add(attribute);
    }

    /**
     * Gets number of attributes (manages inheritance)
     *
     * @return
     */
    public int getAttributeCount() {
        return getParentAttributeCount() + this.attributes.size();
    }

    /**
     * Return number of attributes of parent (0 if parentless)
     *
     * @return
     */
    private int getParentAttributeCount() {
        if (!hasParent()) {
            return 0;
        } else {
            return getParent().getAttributeCount();
        }
    }

    /**
     * Gets an attribute by its position (manages inheritance)
     *
     * @param index
     * @return
     */
    public AttributeType<?> getAttribute(int index) {
        if (index < 0 || index >= getAttributeCount()) {
            throw new IllegalArgumentException("argument index invalide (must be >= 0)");
        }
        if (index < getParentAttributeCount()) {
            return getParent().getAttribute(index);
        } else {
            return this.attributes.get(index - getParentAttributeCount());
        }
    }

    /**
     * Gets an attribute by its name (manages inheritance)
     *
     * @param name
     * @return
     */
    public AttributeType<?> getAttribute(String name) {
        int index = indexOf(name);
        if (index < 0) {
            return null;
        }
        return getAttribute(index);
    }

    /**
     * Gets names of attributes
     *
     * @return
     */
    public List<String> getAttributeNames() {
        List<String> result = new ArrayList<>(getAttributeCount());
        for (int i = 0; i < getAttributeCount(); i++) {
            result.add(getAttribute(i).getName());
        }
        return result;
    }

    /**
     *
     * @return
     */
    public FeatureTypeConstraints getConstraints() {
        return constraints;
    }

    /**
     *
     * @param constraints
     */
    public void setConstraints(FeatureTypeConstraints constraints) {
        this.constraints = constraints;
    }

    /**
     * Retreive the attribute providing the featureId. ---- TODO must add an
     * identifier constraint on type instead of looping for unique constraint ----
     * NOW we skip the WKT attribute as an identifier
     *
     * @return
     */
    public AttributeType<?> getIdentifier() {
        for (AttributeType<?> attribute : attributes) {
            if (attribute.getName().equals("WKT")) {
                continue;
            }
            if (attribute.getConstraints().isUnique()) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * Finds the position of an attribute by its name (manages inheritance)
     *
     * @param name
     * @return -1 si undefined
     */
    public int indexOf(String name) {
        String regexp = "(?i)" + name;
        for (int i = 0; i < attributes.size(); i++) {
            if (attributes.get(i).getName().matches(regexp)) {
                return getParentAttributeCount() + i;
            }
        }
        if (hasParent()) {
            return getParent().indexOf(name);
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return name + " (" + getClass().getSimpleName() + ")";
    }

}
