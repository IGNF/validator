package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiLineString;

import com.fasterxml.jackson.annotation.JsonTypeName;

import org.locationtech.jts.geom.LineString;

@JsonTypeName(MultiLineStringType.TYPE)
public class MultiLineStringType extends GeometryType {

    public static final String TYPE = "MultiLineString";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);
        if (null == geometry || geometry.isEmpty() || geometry instanceof MultiLineString) {
            return geometry;
        } else if (geometry instanceof LineString) {
            LineString[] lineStrings = new LineString[] {
                (LineString) geometry
            };
            return geometry.getFactory().createMultiLineString(lineStrings);
        } else {
            throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
        }
    }

}
