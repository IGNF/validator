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
import fr.ign.validator.model.FeatureType;
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
public class GeometryInsideDocumentValidator implements Validator<Attribute<Geometry>>, ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GeometryInsideDocumentValidator");

//	private Projection sourceProjection;

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

//		sourceProjection = context.getProjection();

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
        System.err.println(MARKER);

        if (context.getDocumentEmprise() == null) {
            log.info(MARKER, "Skip GeometryInsideDocumentValidator. DocumentEmprise is not set");
            return;
        }

        List<TableModel> tableModels = ModelHelper.getTableModels(document.getDocumentModel());
        for (TableModel tableModel : tableModels) {
            FeatureType featureType = tableModel.getFeatureType();
            GeometryType geometryType = featureType.getDefaultGeometry();
            if (geometryType == null) {
                log.info(MARKER, "Skip GeometryInsideDocumentValidator. featureType {}", featureType.getName());
                continue;
            }
            log.info(
                MARKER, "Add GeometryInsideDocumentValidator to featureType {}:{}", featureType.getName(),
                geometryType.getName()
            );
            geometryType.addValidator(new GeometryInsideDocumentValidator());
        }

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        // nothing to do
    }

}
