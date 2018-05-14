package fr.ign.validator.validation.attribute;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates a geometry
 * 
 * @author MBorne
 *
 */
public class GeometryIsValidValidator implements Validator<Attribute<Geometry>> {

	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {
		Geometry geometry = attribute.getBindedValue() ;
		
		if ( null == geometry ){
			return ;
		}

		if ( ! geometry.isValid() ){
			context.report(
				CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID
			);
		}
	}
	
}
