package fr.ign.validator.dgpr.validation.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class ScenarioValidator implements Validator<Database> {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("ScenarioValidator");

	private Context context;
	private Database database;


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


	public void runValidation() throws Exception {
		validateTable("N_prefixTri_ISO_HT_suffixIsoHt_S_ddd", "ID_ZONE");
		validateTable("N_prefixTri_ISO_COTE_L_ddd", "ID_LIGNE");
	}
	
	private void validateTable(String tablename, String attributeId) throws Exception {
		// select / join
		RowIterator inondTable = database.query(
				"SELECT iso." + attributeId + ","
				+ " iso.SCENARIO,"
				+ " iso.WKT,"
				+ " iso.ID_S_INOND,"
				+ " surface.SCENARIO as S_INOND_SCN"
				+ " FROM " + tablename + " as iso "
				+ " JOIN N_prefixTri_INONDABLE_suffixInond_S_ddd as surface "
				+ " ON surface.ID_S_INOND LIKE iso.ID_S_INOND "
		);

		// Indexes
		int indexIsoId = inondTable.getColumn(attributeId);
		int indexIsoScenario = inondTable.getColumn("SCENARIO");
		int indexIsoSinondScenario = inondTable.getColumn("ID_S_INOND");
		int indexSurfaceScenario = inondTable.getColumn("S_INOND_SCN");
		int indexWtk = inondTable.getColumn("WKT");

		if (indexIsoId == -1
			|| indexIsoScenario == -1
			|| indexIsoSinondScenario == -1
			|| indexSurfaceScenario == -1
		) {
			log.error(MARKER, "Some mandatory attibrute id missing");
			return;
		}

		while (inondTable.hasNext()) {
			String[] row = inondTable.next();

			// test de la validité des liens entre les 2 tables
			if (validateScenario(row[indexIsoScenario], row[indexSurfaceScenario])) {
				continue;
			}
			
			Envelope envelope = null;
			if (indexWtk != -1) {
				envelope = DatabaseUtils.getEnveloppe(row[indexWtk], context.getCoordinateReferenceSystem());
			}

			context.report(context.createError(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO)
					.setScope(ErrorScope.FEATURE)
					.setFileModel(tablename)
					.setAttribute("SCENARIO")
					.setFeatureId(row[indexIsoId])
					.setFeatureBbox(envelope)
					.setMessageParam("ID", row[indexIsoId])
					.setMessageParam("TABLE_NAME", tablename)
					.setMessageParam("VALUE_SCENARIO", row[indexIsoScenario])
					.setMessageParam("ID_S_INOND", row[indexIsoSinondScenario])
					.setMessageParam("EXPECTED_SCENARIO", row[indexSurfaceScenario])
			);
		}
	}

	/**
	 *  Test de l'ppartenance d'une surfaces et de la zone au même scénario
	 * @param isoId
	 * @param isoScn
	 * @param surfaceId
	 * @param surfaceScn
	 * @return 
	 */
	private boolean validateScenario(String isoScn, String surfaceScn) {

		if(isoScn == null || surfaceScn == null) {
			log.debug(MARKER, "Attention un des éléments des tables N_prefixTri_ISO_HT_suffixIsoHt_S_ddd ou N_prefixTri_INONDABLE_suffixInond_S_ddd est null");
			return true;
		}

		return isoScn.equals(surfaceScn);
	}

}
