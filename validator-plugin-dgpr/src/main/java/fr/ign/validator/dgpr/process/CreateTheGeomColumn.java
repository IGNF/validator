package fr.ign.validator.dgpr.process;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.GeometryType;

/**
 * Add column the_geom from WKT to all spatial tables.
 * 
 * @author CBouche
 * @author MBorne
 *
 */
public class CreateTheGeomColumn implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("CreateTheGeomColumn");

    public static final String DEFAULT_SRID = "4326";

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        String sourceSrid = context.getProjection().getSrid();
        Database database = Database.createDatabase(context, false);
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            database.close();
            return;
        }

        log.info(MARKER, "Create the_geom column for all tables with a geometry...");
        for (FileModel fileModel : document.getDocumentModel().getFileModels()) {
            if (!(fileModel instanceof TableModel)) {
                continue;
            }
            String tableName = fileModel.getName();
            GeometryType geometryAttribute = fileModel.getFeatureType().getDefaultGeometry();
            if (geometryAttribute == null) {
                log.info(MARKER, "skip {} (not spatial)", tableName);
                continue;
            }

            log.info(MARKER, "Add column the_geom to {}...", tableName);
            database.query(
                "ALTER TABLE " + tableName + " ADD COLUMN "
                    + "the_geom geometry(" + geometryAttribute.getTypeName() + "," + DEFAULT_SRID + ")"
            );

            log.info(MARKER, "Update values for the_geom of {}...", tableName);
            // TODO ensure that ST_Multi is required (kept from original code)
            database.query(
                "UPDATE " + tableName + " SET the_geom = "
                    + "ST_Multi(ST_Transform("
                    + "ST_SetSRID(wkt, " + sourceSrid + ")"
                    + ", 4326))"
            );
        }

        database.getConnection().commit();
        database.close();
    }

}
