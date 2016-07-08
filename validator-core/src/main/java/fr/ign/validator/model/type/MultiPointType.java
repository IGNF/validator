package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;

public class MultiPointType extends GeometryType {
	
	@Override
	public String getTypeName() {
		return "MultiPoint" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value);
		if ( null == geometry || geometry.isEmpty() || geometry instanceof MultiPoint ){
			return geometry ;
		}else if ( geometry instanceof Point ){
			Point[] points = new Point[]{
				(Point)geometry
			};
			return geometry.getFactory().createMultiPoint(points);
		}else{
			throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
		}
	}
	
}
