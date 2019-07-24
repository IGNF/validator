package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

public class PolygonType extends GeometryType {

	@Override
	public String getTypeName() {
		return "Polygon" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value) ;
		return extractOneFromCollection(geometry, Polygon.class);
	}
	
}
