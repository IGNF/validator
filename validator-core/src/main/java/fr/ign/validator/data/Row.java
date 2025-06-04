package fr.ign.validator.data;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.validation.Validatable;

/**
 *
 * Data from a line of a table
 *
 * @author MBorne
 *
 */
public class Row implements Validatable {

    /**
     * Line number
     */
    private int line;

    /**
     * Line values
     */
    private String[] values;

    /**
     * Mapping with type (Result of header validation)
     */
    private FeatureTypeMapper mapping;

    /**
     * The CRS:84 bounding box of the default geometry (retrieved to create error)
     */
    private Envelope featureBbox;

    /**
     * The value of the feature identifier (retrieved to create error)
     */
    private String featureId;

    /**
     *
     * @param line
     * @param values
     */
    public Row(int line, String[] values, FeatureTypeMapper mapping) {
        this.line = line;
        this.values = values;
        this.mapping = mapping;
    }

    public int getLine() {
        return line;
    }

    public String[] getValues() {
        return values;
    }

    public FeatureTypeMapper getMapping() {
        return mapping;
    }

    public Envelope getFeatureBbox() {
        return featureBbox;
    }

    public void setFeatureBbox(Envelope featureBbox) {
        this.featureBbox = featureBbox;
    }

    public String getFeatureId() {
        return featureId;
    }

    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }

    @Override
    public void validate(Context context) {
        context.beginData(this);
        FeatureType featureType = mapping.getFeatureType();

        /*
         * Looking for featureId if exist
         */
        AttributeType<?> attributeFeatureId = featureType.getIdentifier();
        if (attributeFeatureId != null) {
            int indexFeatureId = mapping.getColumnIndex(attributeFeatureId.getName());
            if (indexFeatureId >= 0) {
                this.featureId = values[indexFeatureId];
            }
        }

        /**
         * Looking for geometry if exist
         */
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeType<?> attributeType = featureType.getAttribute(i);
            if (attributeType.isGeometry()) {
                String wkt = null;
                if (mapping.getAttributeIndex(i) >= 0) {
                    wkt = values[mapping.getAttributeIndex(i)];
                } else {
                    continue;
                }
                /* Get bounding box transformed in CRS:84 */
                try {
                    Geometry geom = new ProjectionTransform(context.getProjection()).transformWKT(wkt);
                    setFeatureBbox(geom.getEnvelopeInternal());
                } catch (Exception e) {
                    // TODO logger une info ou un avertissement mineur
                    // impossible de lire la bbox
                }
            }
        }

        /*
         * Validating attributes defined by type
         */
        for (int index = 0; index < featureType.getAttributeCount(); index++) {
            AttributeType<?> attributeType = featureType.getAttribute(index);
            context.beginModel(attributeType);

            /*
             * Retrieving corresponding value in table
             */
            String inputValue = null;
            boolean columnIsAvailable = mapping.getAttributeIndex(index) >= 0;
            if (columnIsAvailable) {
                inputValue = values[mapping.getAttributeIndex(index)];
            }

            /*
             * Attribute validation
             */
            Attribute<?> attribute = attributeType.newAttribute(inputValue);
            /*
             * validating null attributes for all rows is useless (ogr2ogr might remove
             * columns if all values are null/empty)
             */
            if (columnIsAvailable) {
                attribute.validate(context);
            }
            context.endModel(attributeType);
        }

        context.endData(this);
    }

}
