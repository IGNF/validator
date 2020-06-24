package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(GeometryCollectionType.TYPE)
public class GeometryCollectionType extends GeometryType {

    public static final String TYPE = "GeometryCollection";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);
        if (null == geometry || geometry.isEmpty()) {
            return geometry;
        }
        // TODO adapt to normalize to Multi when possible
        if (!geometry.getGeometryType().equals(getTypeName())) {
            throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
        }
        return geometry;
    }

}
