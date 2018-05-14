package fr.ign.validator.validation.attribute;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation de la projection déclarée sur une géométrie en comparaison avec
 * l'emprise de définition de la projection
 * Validates that the geometry fits in data extent 
 * 
 * @author MBorne
 *
 */
public class GeometryDataExtentValidator implements Validator<Attribute<Geometry>> {

	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {
		Geometry geometry = attribute.getBindedValue() ;
		
		if ( null == geometry || geometry.isEmpty() ){
			return ;
		}
		Geometry nativeDataExtent = context.getNativeDataExtent();
		
		if ( nativeDataExtent == null || nativeDataExtent.isEmpty() ){
			return ;
		}
		
		if ( ! nativeDataExtent.contains(geometry) ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT
			);
		}
	}
	
	
}
