package fr.ign.validator.validation.attribute;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.operation.valid.IsValidOp;
import org.locationtech.jts.operation.valid.TopologyValidationError;

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

    @Override
    public void validate(Context context, Attribute<Geometry> attribute) {
        Geometry geometry = attribute.getBindedValue();

        if (null == geometry) {
            return;
        }

        if (!geometry.isValid()) {
            // recherche du point erreur avec la classe IsValidOp
            IsValidOp isValidOp = new IsValidOp(geometry);
            TopologyValidationError topologyValidationError = isValidOp.getValidationError();
            Geometry point = new GeometryFactory().createPoint(topologyValidationError.getCoordinate());
            try {
                Geometry transformPoint = new ProjectionTransform(context.getCoordinateReferenceSystem()).transform(
                    point
                );

                GeometryErrorCode geometryErrorCode = GeometryErrorCode.valueOfJTS(
                    topologyValidationError.getErrorType()
                );
                ValidatorError validatorError = context.createError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID)
                    .setMessageParam("TYPE_ERROR", geometryErrorCode.getMessage())
                    .setErrorGeometry(transformPoint.toText());
                context.report(validatorError);
            } catch (Exception e) {
                context.report(context.createError(CoreErrorCodes.VALIDATOR_EXCEPTION));
            }
        }
    }

}
