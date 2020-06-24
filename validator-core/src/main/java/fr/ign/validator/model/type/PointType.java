package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(PointType.TYPE)
public class PointType extends GeometryType {

    public static final String TYPE = "Point";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);

        return extractOneFromCollection(geometry, Point.class);
    }

}
