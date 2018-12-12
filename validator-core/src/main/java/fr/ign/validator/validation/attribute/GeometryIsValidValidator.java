package fr.ign.validator.validation.attribute;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.operation.valid.IsValidOp;
import com.vividsolutions.jts.operation.valid.TopologyValidationError;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Validates a geometry
 * 
 * @author MBorne
 *
 */
public class GeometryIsValidValidator implements Validator<Attribute<Geometry>> {

	public static String HOLE_OUTSIDE_SHELL = "HOLE_OUTSIDE_SHELL";
	public static String NESTED_HOLES = "NESTED_HOLES";
	public static String DISCONNECTED_INTERIOR = "DISCONNECTED_INTERIOR";
	public static String SELF_INTERSECTION = "SELF_INTERSECTION";
	public static String RING_SELF_INTERSECTION = "RING_SELF_INTERSECTION";
	public static String NESTED_SHELLS = "NESTED_SHELLS";
	public static String DUPLICATE_RINGS = "DUPLICATE_RINGS";
	public static String TOO_FEW_POINTS = "TOO_FEW_POINTS";
	public static String INVALID_COORDINATE = "INVALID_COORDINATE";
	public static String RING_NOT_CLOSED = "RING_NOT_CLOSED";

	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {
		Geometry geometry = attribute.getBindedValue() ;

		if ( null == geometry ) {
			return ;
		}

		if ( ! geometry.isValid() ){
			// recherche du point erreur avec la classe IsValidOp
			IsValidOp isValidOp = new IsValidOp(geometry);
			TopologyValidationError topologyValidationError = isValidOp.getValidationError();
			Geometry point = new GeometryFactory().createPoint(topologyValidationError.getCoordinate());
			try {
				Geometry transformPoint = new ProjectionTransform(context.getCoordinateReferenceSystem()).transform(point);
				ValidatorError validatorError = context.createError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID)
						.setMessageParam("TYPE_ERROR", getTopologyMessage(topologyValidationError.getErrorType()))
						.setErrorGeometry(transformPoint.toText());
				context.report(validatorError);
			} catch (Exception e) {
				context.report(context.createError(CoreErrorCodes.VALIDATOR_EXCEPTION));
			}
		}
	}


	public String getTopologyMessage(int errorType) {
		switch (errorType) {
			case TopologyValidationError.DISCONNECTED_INTERIOR:
				return DISCONNECTED_INTERIOR;

			case TopologyValidationError.DUPLICATE_RINGS:
				return DUPLICATE_RINGS;

			case TopologyValidationError.HOLE_OUTSIDE_SHELL:
				return HOLE_OUTSIDE_SHELL;

			case TopologyValidationError.INVALID_COORDINATE:
				return INVALID_COORDINATE;

			case TopologyValidationError.NESTED_HOLES:
				return NESTED_HOLES;

			case TopologyValidationError.NESTED_SHELLS:
				return NESTED_SHELLS;

			case TopologyValidationError.RING_NOT_CLOSED:
				return RING_NOT_CLOSED;

			case TopologyValidationError.RING_SELF_INTERSECTION:
				return RING_SELF_INTERSECTION;

			case TopologyValidationError.SELF_INTERSECTION:
				return SELF_INTERSECTION;

			case TopologyValidationError.TOO_FEW_POINTS:
				return TOO_FEW_POINTS;
	
			default:
				break;
		}
		return "Unknown error";
	}

}
