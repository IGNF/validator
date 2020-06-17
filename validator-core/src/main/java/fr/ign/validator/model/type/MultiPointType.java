package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(MultiPointType.TYPE)
public class MultiPointType extends GeometryType {

    public static final String TYPE = "MultiPoint";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);
        if (null == geometry || geometry.isEmpty() || geometry instanceof MultiPoint) {
            return geometry;
        } else if (geometry instanceof Point) {
            Point[] points = new Point[] {
                (Point) geometry
            };
            return geometry.getFactory().createMultiPoint(points);
        } else {
            throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
        }
    }

}
