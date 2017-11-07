package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryCollectionType extends GeometryType {

	@Override
	public String getTypeName() {
		return "GeometryCollection" ;
	}
	
	@Override
	public Geometry bind(Object value) {
		Geometry geometry = super.bind(value);
		if ( null == geometry || geometry.isEmpty() ){
			return geometry ;
		}
		//TODO adapt to normalize to Multi when possible
		if ( ! geometry.getGeometryType().equals(getTypeName()) ){
			throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry)) ;
		}
		return geometry ;
	}
	
}
