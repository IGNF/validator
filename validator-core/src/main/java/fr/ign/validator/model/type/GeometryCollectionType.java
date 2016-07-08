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
		//TODO adapter automatiquement pour faire une convertion en Multi quand c'est possible
		if ( ! geometry.getGeometryType().equals(getTypeName()) ){
			throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry)) ;
		}
		return geometry ;
	}
	
}
