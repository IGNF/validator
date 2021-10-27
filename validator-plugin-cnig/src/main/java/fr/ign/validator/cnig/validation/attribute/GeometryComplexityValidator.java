package fr.ign.validator.cnig.validation.attribute;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.geometry.GeometryLength;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

/**
 * Ensure geometry complexity suitable for web broadcasting
 *
 * @author cbouche
 *
 */
public class GeometryComplexityValidator implements Validator<Attribute<Geometry>>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GeometryComplexityValidator");

    private Projection sourceProjection;

    public String isValid(Geometry geometry, int pointCount, int ringCount, int partCount, double density) {

        if (null == geometry) {
            log.debug(MARKER, "Skip validate. geometry is null");
            return "";
        }
 
        if (geometry.getNumPoints() > pointCount) {
        	return String.format("Nombre de sommets %d > %d", geometry.getNumPoints(), pointCount);
        }

        if (geometry.getNumGeometries() > partCount) {
        	return String.format("Nombre de parties %d > %d", geometry.getNumGeometries(), partCount);
        }

        int holeCount = 0;
        for (int i = 0; i < geometry.getNumGeometries(); i++) {
        	if (!(geometry instanceof Polygon)) {
        		continue;
        	}
            holeCount += ((Polygon) geometry.getGeometryN(i)).getNumInteriorRing();
		}

        if (holeCount > ringCount) {
        	return String.format(
        			"Nombre d’anneaux %d > %d",
        			holeCount, ringCount
			);
        }


        for (int i = 0; i < geometry.getNumGeometries(); i++) {
			Geometry part = geometry.getGeometryN(i);
			List<LineString> lineStrings = getGeometryRings(part);
			for (LineString lineString : lineStrings) {
				Double length;
				try {
					length = GeometryLength.getPerimeter(lineString, sourceProjection);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "";
				}
				
				Double lineDensity = (double) (lineString.getNumPoints() / length);
				if (lineDensity > density) {
		        	return String.format("Nombre moyen de point par m %f > %f",  lineDensity, density);
				}
			}
		}

        return "";
    }
    
 
    private List<LineString> getGeometryRings(Geometry geometry) {

    	List<LineString> lineStrings = new ArrayList<LineString>();

    	if (geometry instanceof Polygon) {
			lineStrings.add(((Polygon) geometry).getExteriorRing());
			for (int i = 0; i < ((Polygon) geometry).getNumInteriorRing(); i++) {
				lineStrings.add(((Polygon) geometry).getInteriorRingN(i));
			}
		}
		
    	if (geometry instanceof LineString) {
			lineStrings.add((LineString) geometry);
		}

    	return lineStrings;
    }


	@Override
	public void validate(Context context, Attribute<Geometry> attribute) {

		if (context.getComplexityThreshold() == null) {
            log.debug(MARKER, "Skip validate. ComplexityThreshold is not set.");
			return;
		}

		sourceProjection = context.getProjection();

        Geometry geometry = attribute.getBindedValue();

        String errorType = isValid(
        		geometry,
        		context.getComplexityThreshold().getErrorPointCount(),
        		context.getComplexityThreshold().getErrorRingCount(),
        		context.getComplexityThreshold().getErrorPartCount(),
        		context.getComplexityThreshold().getErrorDensity()
		);

        if (!StringUtils.isEmpty(errorType)) {
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

        if (!StringUtils.isEmpty(errorType)) {
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

		if (context.getComplexityThreshold() == null) {
            log.info(MARKER, "Skip GeometryIsStreamableValidator. ComplexityThreshold is not set");
			return;
		}

		List<TableModel> tableModels = ModelHelper.getTableModels(document.getDocumentModel());
		for (TableModel tableModel : tableModels) {
		    FeatureType featureType = tableModel.getFeatureType();
            GeometryType geometryType = featureType.getDefaultGeometry();
            if (geometryType == null) {
	            log.info(MARKER, "Skip GeometryIsStreamableValidator. featureType {}", featureType.getName());
            	continue;
            }
            log.info(MARKER, "Add GeometryIsStreamableValidator to featureType {}:{}", featureType.getName(), geometryType.getName());
            geometryType.addValidator(new GeometryComplexityValidator());
		}

	}


	@Override
	public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
	}

}
