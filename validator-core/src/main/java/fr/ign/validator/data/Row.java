package fr.ign.validator.data;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.Context;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.type.GeometryType;
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
     * True if featureBbox is computed (or explicitly defined)
     */
    private boolean featureBboxComputed = false;

    /**
     * Geometries of the row (in source projection) from which featureBbox is
     * lazily computed (the costly reprojection is only performed when an error is
     * reported for the row)
     */
    private List<Geometry> featureGeometries = new ArrayList<>();

    /**
     * Source projection of featureGeometries
     */
    private Projection sourceProjection;

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

    /**
     * Get the bounding box of the feature in CRS:84 (computed on first call from
     * the geometries read by {@link #validate(Context)})
     *
     * @return
     */
    public Envelope getFeatureBbox() {
        if (!featureBboxComputed) {
            featureBboxComputed = true;
            featureBbox = computeFeatureBbox();
        }
        return featureBbox;
    }

    public void setFeatureBbox(Envelope featureBbox) {
        this.featureBbox = featureBbox;
        this.featureBboxComputed = true;
    }

    /**
     * Bounding box of the last geometry that can be transformed to CRS:84
     *
     * @return
     */
    private Envelope computeFeatureBbox() {
        for (int i = featureGeometries.size() - 1; i >= 0; i--) {
            try {
                Geometry geom = new ProjectionTransform(sourceProjection).transform(featureGeometries.get(i));
                return geom.getEnvelopeInternal();
            } catch (Exception e) {
                // TODO logger une info ou un avertissement mineur
                // impossible de lire la bbox
            }
        }
        return null;
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

        /*
         * Reading geometries once (used for the binding and the bounding box)
         */
        this.sourceProjection = context.getProjection();
        this.featureGeometries.clear();
        Geometry[] parsedGeometries = new Geometry[featureType.getAttributeCount()];
        for (int i = 0; i < featureType.getAttributeCount(); i++) {
            AttributeType<?> attributeType = featureType.getAttribute(i);
            if (!attributeType.isGeometry() || mapping.getAttributeIndex(i) < 0) {
                continue;
            }
            String wkt = values[mapping.getAttributeIndex(i)];
            try {
                parsedGeometries[i] = GeometryType.parse(wkt);
            } catch (ParseException | RuntimeException e) {
                /*
                 * the binding fails with the WKT (reported as
                 * ATTRIBUTE_GEOMETRY_INVALID_FORMAT). Note that JTS throws
                 * IllegalArgumentException for unclosed rings,...
                 */
                continue;
            }
            if (parsedGeometries[i] != null) {
                featureGeometries.add(parsedGeometries[i]);
            }
        }
        if (!featureGeometries.isEmpty()) {
            // bounding box computed from the geometries on demand
            this.featureBbox = null;
            this.featureBboxComputed = false;
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
            Attribute<?> attribute;
            if (parsedGeometries[index] != null) {
                attribute = attributeType.newAttribute(inputValue, parsedGeometries[index]);
            } else {
                attribute = attributeType.newAttribute(inputValue);
            }
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
