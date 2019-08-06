package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

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
