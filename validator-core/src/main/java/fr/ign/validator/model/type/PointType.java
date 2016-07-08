package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointType extends GeometryType {

	@Override
	public String getTypeName() {
		return "Point" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value) ;
		
		return extractOneFromCollection(geometry,Point.class);
	}

	
		
}
