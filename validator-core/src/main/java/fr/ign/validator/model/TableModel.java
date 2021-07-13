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

}
