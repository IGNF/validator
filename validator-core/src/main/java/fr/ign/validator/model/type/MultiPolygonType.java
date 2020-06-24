package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName(MultiPolygonType.TYPE)
public class MultiPolygonType extends GeometryType {

    public static final String TYPE = "MultiPolygon";

    @Override
    public String getTypeName() {
        return TYPE;
    }
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value);
		if ( null == geometry || geometry.isEmpty() || geometry instanceof MultiPolygon ){
			return geometry ;
		}else if ( geometry instanceof Polygon ){
			Polygon[] polygons = new Polygon[]{
				(Polygon)geometry
			};
			return geometry.getFactory().createMultiPolygon(polygons);
		}else{
			throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
		}
	}
	
}
