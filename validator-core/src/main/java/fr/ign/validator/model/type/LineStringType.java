package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;


public class LineStringType extends GeometryType {

	@Override
	public String getTypeName() {
		return "LineString" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value) ;
		return extractOneFromCollection(geometry, LineString.class);
	}
	
}
