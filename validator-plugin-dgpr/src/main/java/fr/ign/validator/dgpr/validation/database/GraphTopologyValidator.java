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
        try {
            runValidation();
        } catch (PSQLException e) {
            // org.postgresql.util.PSQLException:
            // psql exception throw if a geometry is invalid
            reportException(e.toString());
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

    private Boolean isSafeSimplification() {
        return context.isDgprSafeMode();
    }

    private void runValidation() throws Exception {
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            database.close();
            return;
        }

        // creation des geometries dans le systeme sources
        createSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
        createSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        createSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");

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

    private void createSourceGeometry(String tablename) throws SQLException {
        String srid = this.getSrid();
        Double simplify = this.getDistanceSimplification();
        String[] queries = new String[] {
            "ALTER TABLE " + tablename + " ADD COLUMN source_geometry geometry(MultiPolygon, " + srid + ");",
            "CREATE INDEX " + tablename + "_geom_idx ON " + tablename + " USING GIST (source_geometry);",
            "UPDATE " + tablename + " SET source_geometry = ST_Multi(ST_SnapToGrid(ST_Buffer("
                + " ST_SimplifyPreserveTopology("
                + " ST_SetSRID(wkt, " + srid + "), " + simplify + "), 0), 0.01));",
            "UPDATE " + tablename + " SET source_geometry = ST_Multi("
                + " ST_CollectionExtract(ST_makevalid(source_geometry),3))"
                + " WHERE NOT ST_isValid(source_geometry);"
        };

        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];
            RowIterator result = database.query(query);
        }
    }

    private void validSurfaceTopology(String tablename) throws SQLException, IOException {
        String surfaceTablename = "N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD";

        String query = " SELECT query.ID_S_INOND,"
            + "    query.list_zones"
            + " FROM"
            + " ("
            + " SELECT inond.ID_S_INOND,"
            + "     inond.source_geometry AS the_geom_zone,"
            + "     ST_Buffer("
            + "         inond.source_geometry"
            + "     , " + this.getDistanceBuffer() + ") AS the_geom_buffer_zone,"
            + "     string_agg(feature.ID_ZONE, ', ') as list_zones,"
            + "     ST_Multi(ST_Union("
            + "         feature.source_geometry"
            + "     )) AS the_geom_union,"
            + "     ST_Buffer(ST_Multi(ST_Union("
            + "         feature.source_geometry"
            + "     )), " + this.getDistanceBuffer() + ") AS the_geom_buffer_union"
            + "     FROM " + tablename + " AS feature"
            + "     JOIN " + surfaceTablename + " AS inond"
            + "     ON feature.ID_S_INOND LIKE inond.ID_S_INOND"
            + "     GROUP BY inond.ID_S_INOND, inond.source_geometry"
            + " ) query"
            + " WHERE NOT ST_Contains(query.the_geom_buffer_union, query.the_geom_zone)"
            + " OR NOT ST_Contains(query.the_geom_buffer_zone, query.the_geom_union)"
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

    private void validNoIntersection(String tablename) throws SQLException, IOException {
        String query = " SELECT "
            + "   query.id_s_inond, query.id_zone, query.id_compare"
            + " FROM ("
            + "   SELECT "
            + "     f.id_s_inond, f.id_zone, c.id_zone as id_compare,"
            + "     ST_Buffer(f.source_geometry, -" + this.getDistanceBuffer() + ") as geom,"
            + "     c.source_geometry as geom_compare"
            + "   FROM " + tablename + " AS f"
            + "   LEFT JOIN " + tablename + " as c"
            + "   ON f.id_s_inond = c.id_s_inond"
            + "   WHERE f.id_zone > c.id_zone"
            + "   ORDER BY f.id_s_inond, f.id_zone"
            + " ) query"
            + " WHERE ST_Intersects(query.geom, query.geom_compare)"
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
