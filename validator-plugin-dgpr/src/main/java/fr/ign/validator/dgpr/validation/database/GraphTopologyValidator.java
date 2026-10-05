package fr.ign.validator.dgpr.validation.database;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.postgresql.util.PSQLException;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class GraphTopologyValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("GraphTopologyValidator");

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
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            return;
        }
        try {
            /*
             * source geometries are kept for InclusionValidator when the topology controls
             * fail
             */
            if (runInSavepoint(this::createSourceGeometries)) {
                runInSavepoint(this::runValidation);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // en cas d'exception ou a la fin des traitement on retablie les parametres
            // postgres par default
            // SET enable_seqscan TO on;
            toggleGistScanMode(true);
        }
    }

    private String getSrid() {
        if (context.getProjection() == null) {
            return "2154";
        }
        return context.getProjection().getSrid();
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

    /**
     * Run a task reporting PSQLException (ex : invalid geometry) as
     * DGPR_ISO_HT_GEOM_ERROR. The transaction is rolled back to a savepoint so that
     * the following requests can be performed.
     *
     * @param task
     * @return false if the task failed
     */
    private boolean runInSavepoint(Database.SqlTask task) throws SQLException, IOException {
        try {
            database.runInSavepoint(task);
            return true;
        } catch (PSQLException e) {
            // psql exception throw if a geometry is invalid
            reportException(e.toString());
            return false;
        }
    }

    // creation des geometries dans le systeme sources
    private void createSourceGeometries() throws SQLException {
        createSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
        createSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        createSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");
    }

    private void runValidation() throws SQLException, IOException {
        // force geom gist usage
        // SET enable_seqscan TO off;
        toggleGistScanMode(false);

        // validation de N_prefixTri_ISO_HT_suffixIsoHt_S_ddd
        validSurfaceTopology("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        validNoIntersection("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");

        // validation de N_prefixTri_ISO_DEB_S_ddd
        validSurfaceTopology("N_PREFIXTRI_ISO_DEB_S_DDD");
        validNoIntersection("N_PREFIXTRI_ISO_DEB_S_DDD");

        // TODO : supprimer les geometries dans un processus à part
        // - pour la suppression voir InclusionValidator - exécuter après cf.
        // CustomizeDatabaseValidation
        // suppressions des geometries dans le systeme source
        // dropSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
        // dropSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        // dropSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");
    }

    /**
     * SQL expression computing source_geometry from wkt (simplified if a distance
     * is provided)
     *
     * @param srid
     * @param simplify
     * @return
     */
    static String getSourceGeometryExpression(String srid, Double simplify) {
        String geometry = "ST_SetSRID(wkt, " + srid + ")";
        if (simplify != null) {
            // ST_SimplifyPreserveTopology(geom, NULL) returns NULL
            geometry = "ST_SimplifyPreserveTopology(" + geometry + ", " + simplify + ")";
        }
        return "ST_Multi(ST_SnapToGrid(ST_Buffer(" + geometry + ", 0), 0.01))";
    }

    private void createSourceGeometry(String tablename) throws SQLException {
        String srid = this.getSrid();
        Double simplify = this.getDistanceSimplification();
        String[] queries = new String[] {
            "ALTER TABLE " + tablename + " ADD COLUMN source_geometry geometry(MultiPolygon, " + srid + ");",
            "CREATE INDEX " + tablename + "_geom_idx ON " + tablename + " USING GIST (source_geometry);",
            "UPDATE " + tablename + " SET source_geometry = " + getSourceGeometryExpression(srid, simplify) + ";",
            "UPDATE " + tablename + " SET source_geometry = ST_Multi("
                + " ST_CollectionExtract(ST_makevalid(source_geometry),3))"
                + " WHERE NOT ST_isValid(source_geometry);"
        };

        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];
            RowIterator result = database.query(query);
        }
    }

    /**
     * The union of the zones of a surface must be the surface (with the tolerance
     * distanceBuffer, in both directions).
     *
     * Performance (large datasets, ex : 19 290 zones for a surface) :
     * <ul>
     * <li>the union is computed once per surface, grouped by ID_S_INOND (not by
     * the geometry of the surface)</li>
     * <li>the inclusion is tested without buffer first, the costly buffer is only
     * computed when it fails (same result as the buffer contains the geometry)</li>
     * </ul>
     */
    private void validSurfaceTopology(String tablename) throws SQLException, IOException {
        String surfaceTablename = "N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD";
        double distanceBuffer = this.getDistanceBuffer();

        String query = "WITH unions AS MATERIALIZED ("
            + "   SELECT feature.ID_S_INOND,"
            + "     string_agg(feature.ID_ZONE, ', ') AS list_zones,"
            + "     ST_Multi(ST_Union(feature.source_geometry)) AS the_geom_union"
            + "   FROM " + tablename + " AS feature"
            + "   WHERE feature.ID_S_INOND IN (SELECT ID_S_INOND FROM " + surfaceTablename + ")"
            + "   GROUP BY feature.ID_S_INOND"
            + " )"
            + " SELECT inond.ID_S_INOND, unions.list_zones"
            + " FROM " + surfaceTablename + " AS inond"
            + " JOIN unions ON unions.ID_S_INOND = inond.ID_S_INOND"
            + " WHERE NOT (CASE WHEN ST_Contains(unions.the_geom_union, inond.source_geometry) THEN true"
            + "   ELSE ST_Contains(ST_Buffer(unions.the_geom_union, " + distanceBuffer + "), inond.source_geometry) END)"
            + " OR NOT (CASE WHEN ST_Contains(inond.source_geometry, unions.the_geom_union) THEN true"
            + "   ELSE ST_Contains(ST_Buffer(inond.source_geometry, " + distanceBuffer + "), unions.the_geom_union) END)"
            + " ;";

        RowIterator errorIterator = database.query(query);

        int indexId = errorIterator.getColumn("ID_S_INOND");
        int indexZones = errorIterator.getColumn("list_zones");

        while (errorIterator.hasNext()) {
            String[] row = errorIterator.next();
            report(tablename, row[indexId], row[indexZones]);
        }
        errorIterator.close();
    }

    /**
     * The zones of a surface must not intersect (with the tolerance distanceBuffer).
     *
     * Performance (large datasets) : the negative buffer is computed once per zone
     * (not for each pair of zones) and the pairs are filtered with the spatial index
     * (&&).
     */
    private void validNoIntersection(String tablename) throws SQLException, IOException {
        String query = "WITH f AS MATERIALIZED ("
            + "   SELECT id_s_inond, id_zone, ST_Buffer(source_geometry, -" + this.getDistanceBuffer() + ") AS geom"
            + "   FROM " + tablename
            + " )"
            + " SELECT f.id_s_inond, f.id_zone, c.id_zone AS id_compare"
            + " FROM f"
            + " JOIN " + tablename + " AS c"
            + "   ON c.id_s_inond = f.id_s_inond"
            + "   AND f.id_zone > c.id_zone"
            + "   AND c.source_geometry && f.geom"
            + " WHERE ST_Intersects(f.geom, c.source_geometry)"
            + " ORDER BY f.id_s_inond, f.id_zone"
            + " ;";

        RowIterator errorIterator = database.query(query);

        int indexId = errorIterator.getColumn("ID_S_INOND");
        int indexZone = errorIterator.getColumn("id_zone");
        int indexCompare = errorIterator.getColumn("id_compare");

        while (errorIterator.hasNext()) {
            String[] row = errorIterator.next();
            String zones = row[indexZone] + " " + row[indexCompare];
            reportIntersection(tablename, row[indexId], zones);
        }
        errorIterator.close();
    }

    private void report(String tablename, String surfaceId, String zones) {
        // TODO retablir la BBOX ??
        // .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(),
        // context.getCoordinateReferenceSystem()))
        context.report(
            context.createError(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setFeatureId(surfaceId)
                .setMessageParam("TABLE_NAME", getShortName(tablename))
                .setMessageParam("ID_S_INOND", surfaceId)
                .setMessageParam("LIST_ID_ISO_HT", zones)
        );
    }

    private void reportIntersection(String tablename, String surfaceId, String zones) {
        // TODO retablir la BBOX ??
        // .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(),
        // context.getCoordinateReferenceSystem()))
        context.report(
            context.createError(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setFeatureId(surfaceId)
                .setMessageParam("TABLE_NAME", getShortName(tablename))
                .setMessageParam("ID_S_INOND", surfaceId)
                .setMessageParam("LIST_ID_ISO_HT", zones)
        );
    }

    private void reportException(String errorMessage) {
        // TODO retablir la BBOX ??
        // .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(),
        // context.getCoordinateReferenceSystem()))
        context.report(
            context.createError(DgprErrorCodes.DGPR_ISO_HT_GEOM_ERROR)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
                .setAttribute("WKT")
                .setMessageParam("POSTGIS_ERROR", errorMessage)
        );
    }

    private String getShortName(String tablename) {
        if (tablename.equals("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD")) {
            return "ISO_HT";
        }
        if (tablename.equals("N_PREFIXTRI_ISO_DEB_S_DDD")) {
            return "ISO_DEB";
        }
        return "NULL";
    }

    private void toggleGistScanMode(Boolean mode) {
        String query = "SET enable_seqscan TO off;";
        if (mode) {
            query = "SET enable_seqscan TO on;";
        }
        try {
            RowIterator result = database.query(query);
        } catch (Exception e) {
            //
        }
    }

}
