package fr.ign.validator.dgpr.validation.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

public class InclusionValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("InclusionValidator");

    /**
     * Context
     */
    private Context context;

    /**
     * Document
     */
    private Database database;

    /**
     * Ensure feature of high risk are included in any feature of a lower risk
     *
     * @param context
     * @param document
     * @param database
     */
    @Override
    public void validate(Context context, Database database) {
        // context
        this.context = context;
        this.database = database;
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            return;
        }
        try {
            if (!hasSourceGeometry()) {
                // GraphTopologyValidator failed to create source geometries (already reported)
                log.warn(MARKER, "skipped (source_geometry not found)");
                return;
            }
            try {
                /*
                 * rollback to a savepoint so that the source geometries can be removed
                 */
                database.runInSavepoint(this::runValidation);
            } catch (PSQLException e) {
                // org.postgresql.util.PSQLException:
                // psql exception throw if a geometry is invalid
                reportException(e.toString());
            }
            // suppressions des geometries dans le systeme source
            dropSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
            dropSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
            dropSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            // en cas d'exception ou a la fin des traitement on retablie les parametres
            // postgres par default
            // SET enable_seqscan TO on;
            toggleGistScanMode(true);
        }
    }

    /**
     * True if source_geometry has been created by GraphTopologyValidator
     *
     * @return
     */
    private boolean hasSourceGeometry() throws SQLException, IOException {
        String query = "SELECT count(*) FROM information_schema.columns"
            + " WHERE table_schema = current_schema()"
            + " AND table_name = 'n_prefixtri_inondable_suffixinond_s_ddd'"
            + " AND column_name = 'source_geometry'";
        try (RowIterator it = database.query(query)) {
            return it.hasNext() && Integer.parseInt(it.next()[0]) > 0;
        }
    }

    private void runValidation() throws SQLException, IOException {
        // force geom gist usage
        // SET enable_seqscan TO off;
        toggleGistScanMode(false);

        // TODO : creer les geometries dans un processus à part
        // - pour la creation voir GraphTopologyValidator - exécuter avant cf.
        // CustomizeDatabaseValidation
        // creation des geometries dans le systeme sources
        // createSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
        // createSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        // createSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");

        // validation des surfaces de N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD
        validInclusion();
    }

    /**
     * Pairs of scenarios (stronger, weaker) : the surfaces of the stronger scenario
     * must be included in the union of the surfaces of each weaker scenario (Fort
     * in Moyen, Moyen in Faible and so Fort in Faible).
     */
    private static final String[][] SCENARIO_PAIRS = {
        {
            "01For", "02Moy"
        }, {
            "01For", "04Fai"
        }, {
            "02Moy", "04Fai"
        }, {
            "01Forcc_ct", "03Mcc_ct"
        }, {
            "01Forcc_ct", "04Faicc_ct"
        }, {
            "03Mcc_ct", "04Faicc_ct"
        }, {
            "01Forcc_100", "03Mcc"
        }
    };

    private void validInclusion() throws SQLException, IOException {
        String surfaceTablename = "N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD";

        // same default as GraphTopologyValidator when the tolerance is not defined
        double distanceBuffer = context.getDgprTolerance() != null ? context.getDgprTolerance() : 0.0;

        List<String> pairs = new ArrayList<>();
        for (String[] pair : SCENARIO_PAIRS) {
            pairs.add("('" + pair[0] + "', '" + pair[1] + "')");
        }

        /*
         * a surface may be covered by several surfaces of the weaker scenario (union)
         */
        String query = "WITH pairs(scenario_fort, scenario_faible) AS (VALUES " + String.join(", ", pairs) + "),"
            + " faible AS ("
            + "   SELECT pairs.scenario_fort, sc_faible.scenario AS scenario_faible,"
            + "     ST_Buffer(ST_Union(sc_faible.source_geometry), " + distanceBuffer + ") AS geom,"
            + "     string_agg(sc_faible.id_s_inond, ', ' ORDER BY sc_faible.id_s_inond) AS list_id"
            + "   FROM pairs JOIN " + surfaceTablename + " AS sc_faible"
            + "     ON sc_faible.scenario = pairs.scenario_faible"
            + "   GROUP BY pairs.scenario_fort, sc_faible.scenario"
            + " )"
            + " SELECT sc_fort.id_s_inond AS id_fort, sc_fort.scenario AS scenario,"
            + "   faible.scenario_faible, faible.list_id"
            + " FROM " + surfaceTablename + " AS sc_fort"
            + " JOIN faible ON faible.scenario_fort = sc_fort.scenario"
            + " WHERE NOT ST_Contains(faible.geom, sc_fort.source_geometry)"
            + " ORDER BY sc_fort.id_s_inond, faible.scenario_faible";

        try (RowIterator it = database.query(query)) {
            int indexId = it.getColumn("id_fort");
            int indexFort = it.getColumn("scenario");
            int indexFaible = it.getColumn("scenario_faible");
            int indexListe = it.getColumn("list_id");
            while (it.hasNext()) {
                String[] row = it.next();
                report(row[indexId], row[indexFort], row[indexFaible], row[indexListe]);
            }
        }
    }

    private void report(String id, String scenarioFort, String ScenarioFaible, String listFaible) {
        // TODO retablir la BBOX ??
        // .setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(),
        // context.getCoordinateReferenceSystem()))
        context.report(
            context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
                .setScope(ErrorScope.FEATURE)
                .setFileModel("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD")
                .setFeatureId(id)
                .setAttribute("WKT")
                .setMessageParam("ID_S_INOND", id)
                .setMessageParam("SCENARIO_VALUE_FORT", scenarioFort)
                .setMessageParam("SCENARIO_VALUE_FAIBLE", ScenarioFaible + " - " + listFaible)
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

    private void dropSourceGeometry(String tablename) throws SQLException {
        String[] queries = new String[] {
            "ALTER TABLE " + tablename + "  DROP COLUMN IF EXISTS source_geometry;",
        };

        for (int i = 0; i < queries.length; i++) {
            String query = queries[i];
            RowIterator result = database.query(query);
        }
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
