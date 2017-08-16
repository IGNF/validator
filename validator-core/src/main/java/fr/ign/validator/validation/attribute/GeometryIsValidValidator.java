package fr.ign.validator.validation.attribute;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validation d'une géométrie
 * 
 * @author MBorne
 *
 */
public class GeometryIsValidValidator implements Validator<Attribute<Geometry>> {

	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {
		Geometry geometry = attribute.getValue() ;
		
		if ( null == geometry ){
			return ;
		}

		if ( ! geometry.isValid() ){
			context.report(
				ErrorCode.ATTRIBUTE_GEOMETRY_INVALID
			);
		}
	}
	
}
