package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.geometry.PolygonPerimeter;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.validation.Validator;

/**
 * Validate a geometry to ensure it will be streamable (complexity control)
 *
 * @author cbouche
 *
 */
public class GeometryIsStreamableValidator implements Validator<Attribute<Geometry>>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GeometryIsStreamable");

    private Projection geometryProjection;

    public String isValid(Geometry geometry, int pointCount, int ringCount, int partCount, double density) {

        if (null == geometry) {
            return "";
        }

        int holeCount = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            holeCount += ((Polygon) geometry.getGeometryN(i)).getNumInteriorRing();
		}
 
        if (geometry.getNumPoints() > pointCount) {
        	return String.format("Nombre de sommets %d > %d", geometry.getNumPoints(), pointCount);
        }

        if (geometry.getNumGeometries() > partCount) {
        	return String.format("Nombre de parties %d > %d", geometry.getNumGeometries(), partCount);
        }

        if (holeCount > ringCount) {
        	return String.format(
        			"Nombre d’anneaux %d > %d",
        			holeCount, ringCount
			);
        }
        
        if (!PolygonPerimeter.isProjectionInMeters(geometryProjection)) {
        	return "";
        }

        Double dst = (double) (geometry.getNumPoints() / PolygonPerimeter.getPerimeter(geometry));

        if (dst > density) {
        	return String.format("Nombre moyen de point par m %f > %f", dst , density);
        }

        return "";
    }


	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {

		if (context.getComplexityThreshold() == null) {
			return;
		}

		geometryProjection = context.getProjection();

        Geometry geometry = attribute.getBindedValue();

        String errorType = isValid(
        		geometry,
        		context.getComplexityThreshold().getErrorPointCount(),
        		context.getComplexityThreshold().getErrorRingCount(),
        		context.getComplexityThreshold().getErrorPartCount(),
        		context.getComplexityThreshold().getErrorDensity()
		);

        if (!errorType.equals("")) {
        	context.report(
                context.createError(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR)
                	.setMessageParam("TYPE_ERROR", errorType)
            );
            return;
        }

        errorType = isValid(
        		geometry,
        		context.getComplexityThreshold().getWarningPointCount(),
        		context.getComplexityThreshold().getWarningRingCount(),
        		context.getComplexityThreshold().getWarningPartCount(),
        		context.getComplexityThreshold().getWarningDensity()
		);

        if (!errorType.equals("")) {
            context.report(
                context.createError(CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING)
                	.setMessageParam("TYPE_ERROR", errorType)
            );
            return;
        }

	}


	@Override
	public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
	}


	@Override
	public void beforeValidate(Context context, Document document) throws Exception {

		List<FileModel> fileModels = document.getDocumentModel().getFileModels();

        for (FileModel fileModel : fileModels) {
            if (fileModel instanceof SingleTableModel) {
                FeatureType featureType = ((TableModel) fileModel).getFeatureType();
                GeometryType geometryType = featureType.getDefaultGeometry();
                if (geometryType == null) {
                	continue;
                }
	            log.info(MARKER, "Ajout de GeometryIsStreamable à {}", geometryType.getName());
	            geometryType.addValidator(new GeometryIsStreamableValidator());
            }
        }

	}


	@Override
	public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
	}

}
