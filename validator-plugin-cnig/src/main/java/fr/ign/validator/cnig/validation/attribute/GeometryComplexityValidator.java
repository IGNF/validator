package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.geometry.GeometryLength;
import fr.ign.validator.geometry.GeometryRings;
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

    private Integer isNumPointInvalid(Geometry geometry, int pointCount) {
        if (geometry.getNumPoints() > pointCount) {
            return geometry.getNumPoints();
        }
        return null;
    }

    private Integer isNumPartInvalid(Geometry geometry, int partCount) {
        if (geometry.getNumGeometries() > partCount) {
            return geometry.getNumGeometries();
        }
        return null;
    }

    private Integer isNumRingInvalid(Geometry geometry, int ringCount) {
        int holeCount = GeometryRings.getInnerRings(geometry).size();
        if (holeCount > ringCount) {
            return holeCount;
        }
        return null;
    }

    private Double isDensityInvalid(Geometry geometry, double density) {
        List<LineString> lineStrings = GeometryRings.getRings(geometry);
        for (LineString lineString : lineStrings) {
            Double length;
            try {
                length = GeometryLength.getPerimeter(lineString, sourceProjection);
            } catch (Exception e) {
                throw new RuntimeException("GeometryComplexityValidator: unable to determine length of a geometry.");
            }
            if (length == 0) {
                continue;
            }
            Double lineDensity = (double) (lineString.getNumPoints() / length);
            if (lineDensity > density) {
                return lineDensity;
            }
        }
        return null;
    }

    /**
     * 
     * cnig-plugin-validator: we allow french usage to message error.
     * 
     * @param geometry
     * @param pointCount
     * @param ringCount
     * @param partCount
     * @param density
     * @return
     */
    public String isWarning(Geometry geometry, int pointCount, int ringCount, int partCount, double density) {

        if (null == geometry) {
            log.debug(MARKER, "Skip validate. geometry is null");
            return "";
        }

        // test1: point count && point density
        {
            Integer numPoint = isNumPointInvalid(geometry, pointCount);
            Double lineDensity = isDensityInvalid(geometry, density);
            if (numPoint != null && lineDensity != null) {
                return String.format(
                    "Nombre de sommets %d > %d et nombre moyen de point par m %f > %f",
                    numPoint,
                    pointCount,
                    lineDensity,
                    density
                );
            }
        }

        // test2: part count
        {
            Integer message = isNumPartInvalid(geometry, partCount);
            if (message != null) {
                return String.format("Nombre de parties %d > %d", message, partCount);
            }
        }

        // test3: ring count
        {
            Integer message = isNumRingInvalid(geometry, ringCount);
            if (message != null) {
                return String.format("Nombre d’anneaux %d > %d", message, ringCount);
            }
        }

        return "";
    }

    /**
     * 
     * @param geometry
     * @param pointCount
     * @param ringCount
     * @param partCount
     * @param density
     * @return
     */
    public String isError(Geometry geometry, int pointCount, int ringCount, int partCount, double density) {

        if (null == geometry) {
            log.debug(MARKER, "Skip validate. geometry is null");
            return "";
        }

        // test1: point count
        {
            Integer message = isNumPointInvalid(geometry, pointCount);
            if (message != null) {
                return String.format("Nombre de sommets %d > %d", message, pointCount);
            }
        }

        // test2: part count
        {
            Integer message = isNumPartInvalid(geometry, partCount);
            if (message != null) {
                return String.format("Nombre de parties %d > %d", message, partCount);
            }
        }

        // test3: ring count
        {
            Integer message = isNumRingInvalid(geometry, ringCount);
            if (message != null) {
                return String.format("Nombre d’anneaux %d > %d", message, ringCount);
            }
        }

        // test4: point density
        {
            Double message = isDensityInvalid(geometry, density);
            if (message != null) {
                return String.format("Nombre moyen de point par m %f > %f", message, density);
            }
        }

        return "";
    }

    @Override
    public void validate(Context context, Attribute<Geometry> attribute) {

        if (context.getComplexityThreshold() == null) {
            log.debug(MARKER, "Skip validate. ComplexityThreshold is not set.");
            return;
        }

        sourceProjection = context.getProjection();

        Geometry geometry = attribute.getBindedValue();

        String errorType = isError(
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

        errorType = isWarning(
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
            log.info(
                MARKER, "Add GeometryIsStreamableValidator to featureType {}:{}", featureType.getName(), geometryType
                    .getName()
            );
            geometryType.addValidator(new GeometryComplexityValidator());
        }

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

}
