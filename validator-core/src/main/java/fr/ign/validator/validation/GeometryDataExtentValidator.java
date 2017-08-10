package fr.ign.validator.validation;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.Validator;

/**
 * 
 * Validation de la projection déclarée sur une géométrie en comparaison avec
 * l'emprise de définition de la projection
 * 
 * @author MBorne
 *
 */
public class GeometryDataExtentValidator implements Validator<Attribute<Geometry>> {

	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {
		Geometry geometry = attribute.getValue() ;
		
		if ( null == geometry || geometry.isEmpty() ){
			return ;
		}
		Geometry nativeDataExtent = context.getNativeDataExtent();
		
		if ( nativeDataExtent == null || nativeDataExtent.isEmpty() ){
			return ;
		}
		
		if ( ! nativeDataExtent.contains(geometry) ){
			context.report(
				ErrorCode.ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT
			);
		}
	}
	
	
}
