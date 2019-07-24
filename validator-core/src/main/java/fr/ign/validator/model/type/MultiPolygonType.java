package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class MultiPolygonType extends GeometryType {
	
	@Override
	public String getTypeName() {
		return "MultiPolygon" ;
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
