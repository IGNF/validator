package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(LineStringType.TYPE)
public class LineStringType extends GeometryType {

    public static final String TYPE = "LineString";

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Geometry bind(Object value) {
        Geometry geometry = super.bind(value);
        return extractOneFromCollection(geometry, LineString.class);
    }

}
