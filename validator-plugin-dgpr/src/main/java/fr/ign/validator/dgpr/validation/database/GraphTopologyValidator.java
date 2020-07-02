package fr.ign.validator.dgpr.validation.database;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.database.model.SurfaceInondable;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class GraphTopologyValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TopologicalGraphValidator");

    /**
     * Context
     */
    private Context context;

    /**
     * Document
     */
    private Database database;

    /**
     * Iso classe de hauteur et débit respectent une topologie de graphe
     * 
     * @param context
     * @param document
     * @param database
     * @throws Exception
     */
    public void validate(Context context, Database database) {
        // context
        this.context = context;
        this.database = database;
        try {
            runValidation();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private double getDistanceBuffer() {
        if (context.getDgprTolerance() == null) {
            return 0.0;
        }
        return context.getDgprTolerance();
    }

    private Double getDistanceSimplification() {
        return context.getDgprSimplification();
    }

    private Boolean isSafeSimplification() {
        return context.isDgprSafeMode();
    }

    private void runValidation() throws Exception {
        RowIterator surfaceIterator = database.query(
            "SELECT ID_S_INOND, WKT FROM N_prefixTri_INONDABLE_suffixInond_S_ddd "
        );

        int indexId = surfaceIterator.getColumn("ID_S_INOND");
        int indexWkt = surfaceIterator.getColumn("WKT");

        if (indexId == -1 || indexWkt == -1) {
            log.warn(
                MARKER,
                "N_prefixTri_INONDABLE_suffixInond_S_ddd - Model ERROR - WKT or/and ID_S_INOND column is/are missing."
            );
            return;
        }

        while (surfaceIterator.hasNext()) {
            String[] row = surfaceIterator.next();
            if (!DatabaseUtils.isValidWKT(row[indexWkt])) {
                log.error(MARKER, "Geometry not valid for surface {}", row[indexId]);
                continue;
            }

            SurfaceInondable surface = new SurfaceInondable(row[indexId], row[indexWkt]);
            try {
                validSurfaceTopology(surface, "N_prefixTri_ISO_HT_suffixIsoHt_S_ddd");
            } catch (OutOfMemoryError error) {
                reportMemoryError(surface, "N_prefixTri_ISO_HT_suffixIsoHt_S_ddd");
            }
            try {
                validSurfaceTopology(surface, "N_prefixTri_ISO_DEB_S_ddd");
            } catch (OutOfMemoryError error) {
                reportMemoryError(surface, "N_prefixTri_ISO_DEB_S_ddd");
            }
        }
        surfaceIterator.close();
    }

    private void validSurfaceTopology(SurfaceInondable surface, String tablename) throws Exception {
        if (surface.getId() == null || surface.getId().equals("null")) {
            log.error(MARKER, "{} - Impossible de valider la topology, identifiant 'null' détecté ", tablename);
            return;
        }
        RowIterator hauteurIterator = database.query(
            "SELECT ID_ZONE, ID_S_INOND, WKT "
                + " FROM " + tablename
                + " WHERE ID_S_INOND LIKE '" + surface.getId() + "' "
        );

        int indexId = hauteurIterator.getColumn("ID_ZONE");
        int indexIdSurface = hauteurIterator.getColumn("ID_S_INOND");
        int indexWkt = hauteurIterator.getColumn("WKT");

        if (indexId == -1 || indexIdSurface == -1 || indexWkt == -1) {
            log.warn(MARKER, "{} - Model ERROR - WKT or/and ID_S_INOND column is/are missing.", tablename);
            return;
        }

        Geometry union = null;
        boolean intersected = false;
        while (hauteurIterator.hasNext()) {
            String[] row = hauteurIterator.next();

            // validate geometry or die
            if (!DatabaseUtils.isValidWKT(row[indexWkt])) {
                log.error(MARKER, "Geometry not valid for object {}", row[indexId]);
                report(surface, tablename);
                hauteurIterator.close();
                return;
            }

            // validate no intersection and continue
            Geometry geometry = DatabaseUtils.getGeometryFromWkt(
                row[indexWkt], getDistanceSimplification(), isSafeSimplification()
            );
            if (!intersected && !noIntersection(union, geometry)) {
                reportIntersection(surface, tablename);
                intersected = true;
            }

            // save new union
            if (union == null) {
                union = geometry;
            } else {
                union = DatabaseUtils.getUnion(union, geometry);
            }

        }
        hauteurIterator.close();

        if (union == null) {
            // nothing to validate
            return;
        }

        // we already know that surfaceGeometry is Valid
        Geometry surfaceGeometry = DatabaseUtils.getGeometryFromWkt(
            surface.getWkt(), getDistanceSimplification(), isSafeSimplification()
        );
        // validate union equalsTopo surface or die
        // tolerance to 1 meters
        if (!topologyEqualsWithTolerance(union, surfaceGeometry, getDistanceBuffer())) {
            report(surface, tablename);
        }
    }

    private boolean noIntersection(Geometry a, Geometry b) {
        if (a == null || b == null) {
            return true;
        }
        Geometry unbufferA = a.buffer(-1 * getDistanceBuffer());
        Geometry unbufferB = b.buffer(-1 * getDistanceBuffer());
        Geometry intersection = unbufferA.intersection(unbufferB);
        if (intersection.getGeometryType().equals("Polygon")
            && intersection.getCoordinate() != null
            || intersection.getGeometryType().equals("MultiPolygon")) {
            return false;
        }
        return true;
    }

    private String findAllHauteur(SurfaceInondable surface, String tablename) throws IOException, SQLException {
        /*
         * IN postgresql there is no GROUP_CONCAT function we use SELECT id,
         * string_agg(some_column, ',') FROM the_table GROUP BY id
         */
        String sql;
        if (database.isPostgresqlDriver()) {
            sql = " SELECT string_agg(ID_ZONE, ', ') as result" +
                " FROM " + tablename +
                " WHERE ID_S_INOND LIKE '" + surface.getId() + "' ";
        } else {
            sql = " SELECT GROUP_CONCAT(ID_ZONE, ', ') as result" +
                " FROM " + tablename +
                " WHERE ID_S_INOND LIKE '" + surface.getId() + "' ";
        }
        RowIterator iterator = database.query(sql);

        int index = iterator.getColumn("result");
        if (index == -1) {
            log.warn(MARKER, "N_prefixTri_ISO_HT_suffixIsoHt_S_ddd - group concat request ERROR");
            return null;
        }

        String result = iterator.next()[0];
        iterator.close();
        return result;
    }

    private String getShortName(String tablename) {
        if (tablename.equals("N_prefixTri_ISO_HT_suffixIsoHt_S_ddd")) {
            return "ISO_HT";
        }
        if (tablename.equals("N_prefixTri_ISO_DEB_S_ddd")) {
            return "ISO_DEB";
        }
        return "NULL";
    }

    private void report(SurfaceInondable surface, String tablename) throws Exception {
        context.report(
            context.createError(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setFeatureId(surface.getId())
                .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(), context.getCoordinateReferenceSystem()))
                .setMessageParam("TABLE_NAME", getShortName(tablename))
                .setMessageParam("ID_S_INOND", surface.getId())
                .setMessageParam("LIST_ID_ISO_HT", findAllHauteur(surface, tablename))
        );
    }

    private void reportIntersection(SurfaceInondable surface, String tablename) throws Exception {
        context.report(
            context.createError(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setFeatureId(surface.getId())
                .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(), context.getCoordinateReferenceSystem()))
                .setMessageParam("TABLE_NAME", getShortName(tablename))
                .setMessageParam("ID_S_INOND", surface.getId())
                .setMessageParam("LIST_ID_ISO_HT", findAllHauteur(surface, tablename))
        );
    }

    private void reportMemoryError(SurfaceInondable surface, String tablename) throws Exception {
        context.report(
            context.createError(DgprErrorCodes.DGPR_GRAPH_VALIDATION_OUT_OF_MEMORY)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setFeatureId(surface.getId())
                .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(), context.getCoordinateReferenceSystem()))
                .setMessageParam("TABLE_NAME", getShortName(tablename))
                .setMessageParam("ID_S_INOND", surface.getId())
        );
    }

    public static boolean topologyEqualsWithTolerance(Geometry a, Geometry b, double distanceBuffer) {
        // same topological geometries
        /*
         * equalsTopo performance issue (for complex geometry may throw a
         * "out of memory exception" if (a.equalsTopo(b)) { return true; }
         */
        Geometry aBuffer = a.buffer(distanceBuffer);
        Geometry bBuffer = b.buffer(distanceBuffer);
        if (!aBuffer.contains(b) || !bBuffer.contains(a)) {
            return false;
        }
        if (geometryHasInteriorPoint(aBuffer) || geometryHasInteriorPoint(bBuffer)) {
            return false;
        }
        return true;
    }

    public static boolean geometryHasInteriorPoint(Geometry geometry) {
        if (geometry instanceof Polygon) {
            if (((Polygon) geometry).getNumInteriorRing() != 0) {
                return true;
            }
        }
        if (geometry instanceof MultiPolygon) {
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Geometry polygon = ((MultiPolygon) geometry).getGeometryN(i);
                if (geometryHasInteriorPoint(polygon)) {
                    return true;
                }
            }
        }
        return false;
    }

}
