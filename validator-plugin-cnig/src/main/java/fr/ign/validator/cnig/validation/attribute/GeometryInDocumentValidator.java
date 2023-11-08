package fr.ign.validator.cnig.validation.attribute;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Document;
import fr.ign.validator.geometry.ProjectionList;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.validation.Validator;

/**
 * Ensure geometry is inside the declared document perimeter.
 *
 * @author cbouche
 *
 */
public class GeometryInDocumentValidator implements Validator<Attribute<Geometry>>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GeometryInDocumentValidator");

    @Override
    public void validate(Context context, Attribute<Geometry> attribute) {

        Geometry documentEmprise = context.getDocumentEmprise();
        if (documentEmprise == null) {
            log.debug(MARKER, "Skip validation. DocumentEmprise is not set.");
            return;
        }

        Geometry geometry = attribute.getBindedValue();
        if (null == geometry) {
            log.debug(MARKER, "Skip validation. Geometry is null");
            return;
        }

        if (isInsideDocumentEmprise(geometry, documentEmprise)) {
            return;
        }
        context.report(context.createError(CnigErrorCodes.CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR));

    }

    private Boolean isInsideDocumentEmprise(Geometry geometry, Geometry emprise) {
        return emprise.contains(geometry);
    }

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {
        // nothing to do
    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

        if (context.getDocumentEmprise() == null) {
            log.info(MARKER, "Skip validator. DocumentEmprise is not set");
            return;
        }

        // transform input geometry
        Projection sourceProjection = context.getProjection();
        Projection crs84 = ProjectionList.getCRS84();
        ProjectionTransform projectionTransform = new ProjectionTransform(crs84, sourceProjection);

        Geometry projectedGeometry = projectionTransform.transform(context.getDocumentEmprise());
        Geometry bufferedGeometry = projectedGeometry.buffer(10);
        context.setDocumentEmprise(bufferedGeometry);

        List<TableModel> tableModels = ModelHelper.getTableModels(document.getDocumentModel());
        for (TableModel tableModel : tableModels) {
            FeatureType featureType = tableModel.getFeatureType();
            GeometryType geometryType = featureType.getDefaultGeometry();
            if (geometryType == null) {
                log.info(MARKER, "Skip {}. No geomety for this featureType.", featureType.getName());
                continue;
            }
            String name = featureType.getName();
            if (name.equals("SECTEUR_CC") || name.equals("ZONE_URBA")) {
                log.info(MARKER, "Skip {}. Only for SECTEUR_CC or ZONE_URBA.", featureType.getName());
                continue;
            }
            log.info(
                MARKER, "Add validator to featureType {}:{}", featureType.getName(),
                geometryType.getName()
            );
            geometryType.addValidator(new GeometryInDocumentValidator());
        }

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

}
