package fr.ign.validator.dgpr.validation.database;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class InclusionValidator implements Validator<Database> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("InclusionDatabaseValidator");

    /**
     * Context
     */
    private Context context;

    /**
     * Document
     */
    private Database database;

    /**
     * WKT Reader Enable projection transform to WKT Geometries
     */
    private static WKTReader format = new WKTReader();

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void runValidation() throws Exception {
        // check feature(01Forcc_ct) -> union(03Mcc_ct)
        validInclusion("01Forcc_ct", "03Mcc_ct");
        // check feature(03Mcc_ct) -> union(04Faicc_ct)
        validInclusion("03Mcc_ct", "04Faicc_ct");

        // check feature(01Forcc_100) -> union(04Faicc_ct)
        validInclusion("01Forcc_100", "03Mcc");

        // check feature(moy) -> union(FAIBLE)
        boolean moyIncludeFaibleError = validInclusion("02Moy", "04Fai");

        // check feature(fort) -> union(MOY)
        boolean fortIncludeMoyError = validInclusion("01For", "02Moy");

        if (moyIncludeFaibleError || fortIncludeMoyError) {
            // check feature(fort) -> union(FAIBLE)
            validInclusion("01For", "04Fai");
        }
    }

    /**
     * Detect that each feature of a given SCENARIO is include in the UNION of a
     * given scenario
     * 
     * @param scenarioValueFort
     * @param scenarioValueFaible
     * @return
     * @throws Exception
     */
    private boolean validInclusion(String scenarioValueFort, String scenarioValueFaible) throws Exception {
        RowIterator unionIterator = database.query(
            "SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
                + " WHERE scenario LIKE '" + scenarioValueFaible + "'"
        );
        Geometry union = DatabaseUtils.getUnion(unionIterator);
        if (union == null) {
            // list of invalid geometry for a unique message
            reportInvalidGeometryError(scenarioValueFort, scenarioValueFaible);
            return false;
        }

        RowIterator featureIterator = database.query(
            "SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
                + " WHERE scenario LIKE '" + scenarioValueFort + "'"
        );
        int wktIndex = featureIterator.getColumn("WKT");
        int idIndex = featureIterator.getColumn("ID_S_INOND");

        boolean atLeastOneError = false;
        while (featureIterator.hasNext()) {
            String[] row = featureIterator.next();
            String wkt = row[wktIndex];

            Geometry geometry = format.read(wkt);
            if (DatabaseUtils.isValid(geometry) && !union.contains(geometry)) {
                context.report(
                    context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
                        .setScope(ErrorScope.FEATURE)
                        .setFileModel("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD")
                        .setFeatureId(row[idIndex])
                        .setFeatureBbox(DatabaseUtils.getEnveloppe(wkt, context.getCoordinateReferenceSystem()))
                        .setAttribute("WKT")
                        .setMessageParam("ID_S_INOND", row[idIndex])
                        .setMessageParam("SCENARIO_VALUE_FORT", scenarioValueFort)
                        .setMessageParam("SCENARIO_VALUE_FAIBLE", scenarioValueFaible)
                );
                atLeastOneError = true;
            }
        }
        featureIterator.close();

        return atLeastOneError;
    }

    private void reportInvalidGeometryError(String scenarioValueFort, String scenarioValueFaible) throws Exception {
        RowIterator rowIterator = database.query(
            "SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
                + " WHERE scenario LIKE '" + scenarioValueFort + "' "
                + " OR scenario LIKE '" + scenarioValueFaible + "' "
        );

        List<String> list = DatabaseUtils.getInvalidGeometries(rowIterator, "ID_S_INOND");

        context.report(
            context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_INVALID_GEOM)
                .setScope(ErrorScope.HEADER)
                .setFileModel("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD")
                .setMessageParam("LIST_ID_S_INOND", ArrayUtils.toString(list.toArray()))
                .setMessageParam("SCENARIO_VALUE_FORT", scenarioValueFort)
                .setMessageParam("SCENARIO_VALUE_FAIBLE", scenarioValueFaible)
        );
    }

}
