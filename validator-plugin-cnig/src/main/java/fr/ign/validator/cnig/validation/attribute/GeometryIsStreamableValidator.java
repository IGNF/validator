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
	
	private String srid;

    public String isValid(Geometry geometry, int pointCount, int ringCount, int partCount, double density) {

        if (null == geometry) {
            return "";
        }

        if (geometry.getNumPoints() > pointCount) {
        	return String.format("Nombre de sommets > %d", pointCount);
        }

        if (((Polygon) geometry).getNumInteriorRing() > ringCount) {
        	return String.format("Nombre d’anneaux > %d", ringCount);
        }

        if (geometry.getNumGeometries() > partCount) {
        	return String.format("Nombre de parties > %d", partCount);
        }

        if (geometry.getNumPoints() / PolygonPerimeter.getPerimeter(geometry, srid) > density) {
        	return String.format("Nombre moyen de point par m > %f", density);
        }

        return "";
    }


	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {

		// get params context SRID
		srid = "2154";
        if (context.getProjection() != null) {
            srid = context.getProjection().getSrid();
        }

        Geometry geometry = attribute.getBindedValue();

        String errorType = isValid(
        		geometry,
        		context.getComplexityThreshold().getErrPoinCnt(),
        		context.getComplexityThreshold().getErrRingCnt(),
        		context.getComplexityThreshold().getErrPartCnt(),
        		context.getComplexityThreshold().getErrDensity()
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
        		context.getComplexityThreshold().getWarnPoinCnt(),
        		context.getComplexityThreshold().getWarnRingCnt(),
        		context.getComplexityThreshold().getWarnPartCnt(),
        		context.getComplexityThreshold().getWarnDensity()
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
