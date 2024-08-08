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

    private void runValidation() throws Exception {
        if (!database.hasGeometrySupport()) {
            log.info(MARKER, "skipped for non postgis database");
            database.close();
            return;
        }

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

        // suppressions des geometries dans le systeme source
        dropSourceGeometry("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
        dropSourceGeometry("N_PREFIXTRI_ISO_HT_SUFFIXISOHT_S_DDD");
        dropSourceGeometry("N_PREFIXTRI_ISO_DEB_S_DDD");
    }

    private void validInclusion() throws SQLException, IOException {
        String surfaceTablename = "N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD";

        double distanceBuffer = context.getDgprTolerance();

        String query = " SELECT "
            + "   sc_fort.id_s_inond as id_fort"
            + " FROM "
            + "   (SELECT * FROM " + surfaceTablename + ") sc_fort"
            + "   JOIN " + surfaceTablename + " AS sc_faible"
            + "   ON sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01For'"
            + "     AND sc_faible.scenario LIKE '02Moy'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '02Moy'"
            + "     AND sc_faible.scenario LIKE '04Fai'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01Forcc_ct'"
            + "     AND sc_faible.scenario LIKE '03Mcc_ct'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '03Mcc_ct'"
            + "     AND sc_faible.scenario LIKE '04Faicc_ct'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01Forcc_100'"
            + "     AND sc_faible.scenario LIKE '03Mcc'"
            + " WHERE ST_Contains("
            + "         ST_Buffer(sc_faible.source_geometry, " + distanceBuffer + "),"
            + "         sc_fort.source_geometry"
            + "     );";

        // TODO examiner la liste avec la liste complète des 'scenario fort'
        // chaque 'scenario fort' doit être inclus dans <au moin> un 'scenario faible
        // correpondant'
        RowIterator inclusionIterator = database.query(query);

        int inclusionId = inclusionIterator.getColumn("id_fort");

        String candidate = "";
        while (inclusionIterator.hasNext()) {
            String[] row = inclusionIterator.next();
            candidate += row[inclusionId] + ", ";
        }
        inclusionIterator.close();

        String querySurface = " SELECT "
            + "   sc_fort.id_s_inond as id_fort,"
            + "   sc_fort.scenario as scenario,"
            + "   sc_faible.scenario as scenario_faible,"
            + "   string_agg(sc_faible.id_s_inond, ', ') as list_id"
            + " FROM "
            + "   (SELECT * FROM " + surfaceTablename + ") sc_fort"
            + "   JOIN " + surfaceTablename + " AS sc_faible"
            + "   ON sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01For' "
            + "      AND sc_faible.scenario LIKE '02Moy'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '02Moy' "
            + "      AND sc_faible.scenario LIKE '04Fai'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01Forcc_ct' "
            + "      AND sc_faible.scenario LIKE '03Mcc_ct'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '03Mcc_ct' "
            + "      AND sc_faible.scenario LIKE '04Faicc_ct'"
            + "   OR sc_fort.scenario < sc_faible.scenario AND sc_fort.scenario LIKE '01Forcc_100' "
            + "      AND sc_faible.scenario LIKE '03Mcc'"
            + " GROUP BY sc_fort.id_s_inond, sc_fort.scenario, sc_faible.scenario"
            + " ;";

        // TODO examiner la liste avec la liste complète des 'scenario fort'
        // chaque 'scenario fort' doit être inclus dans <au moin> un 'scenario faible
        // correpondant'
        RowIterator inondableIterator = database.query(querySurface);

        int inondableId = inondableIterator.getColumn("id_fort");
        int inondableFort = inondableIterator.getColumn("scenario");
        int inondableFaible = inondableIterator.getColumn("scenario_faible");
        int inondableListe = inondableIterator.getColumn("list_id");

        while (inondableIterator.hasNext()) {
            String[] row = inondableIterator.next();
            // if list of candidate contains the current id
            // then the surface is good to go
            if (candidate.contains(row[inondableId])) {
                continue;
            }
            report(row[inondableId], row[inondableFort], row[inondableFaible], row[inondableListe]);
        }
        inondableIterator.close();
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
