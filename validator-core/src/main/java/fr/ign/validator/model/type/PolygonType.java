package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

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
