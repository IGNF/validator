package fr.ign.validator.model;

/**
 * Base interface for models describing a table.
 *
 * @author MBorne
 *
 */
public interface TableModel extends Model {

    /**
     * Get FeatureType describing the table.
     *
     * @return
     */
    public FeatureTypeRef getFeatureTypeRef();

    /**
     * Get FeatureType describing the table.
     *
     * @return
     */
    public FeatureType getFeatureType();

    /**
     * Set FeatureType describing the table.
     *
     * @param featureType
     */
    public void setFeatureType(FeatureType featureType);

}
