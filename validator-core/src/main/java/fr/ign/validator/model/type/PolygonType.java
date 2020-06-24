package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(PolygonType.TYPE)
public class PolygonType extends GeometryType {

    public static final String TYPE = "Polygon";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);
        return extractOneFromCollection(geometry, Polygon.class);
    }

}
