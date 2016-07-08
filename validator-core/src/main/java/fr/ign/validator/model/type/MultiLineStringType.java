package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.LineString;

public class MultiLineStringType extends GeometryType {

	@Override
	public String getTypeName() {
		return "MultiLineString" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value);
		if ( null == geometry || geometry.isEmpty() || geometry instanceof MultiLineString ){
			return geometry ;
		}else if ( geometry instanceof LineString ){
			LineString[] lineStrings = new LineString[]{
				(LineString)geometry
			};
			return geometry.getFactory().createMultiLineString(lineStrings);
		}else{
			throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
		}
	}
	
}
