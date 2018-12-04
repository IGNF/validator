package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;

public class ScenarioValidator {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("ScenarioValidator");
	
	private Context context;
	private Database database;
	
	public void validate(Context context, Document document, Database database) throws SQLException {
				
		// context
		this.context = context;
		this.database = database;
		
		//Selection via union des tables
		RowIterator inondTable = database.query(
				"SELECT iso.ID_ZONE, iso.SCENARIO, iso.ID_S_INOND, surface.SCENARIO as S_INOND_SCN FROM N_prefixTri_ISO_HT_suffixIsoHt_S_ddd as iso " +
				"JOIN N_prefixTri_INONDABLE_suffixInond_S_ddd as surface " +
				"ON surface.ID_S_INOND LIKE iso.ID_S_INOND "
		);

		// Index des champs
		int indexIsoId = inondTable.getColumn("ID_ZONE");
		int indexIsoScenario = inondTable.getColumn("SCENARIO");
		int indexIsoSinondScenario = inondTable.getColumn("ID_S_INOND");
		int indexSurfaceScenario = inondTable.getColumn("S_INOND_SCN");
		
		while (inondTable.hasNext()) {
			String[] row = inondTable.next();	
			
			//test de la validité des liens entre les 2 tables
			validateScenario(row[indexIsoId], row[indexIsoScenario], row[indexIsoSinondScenario], row[indexSurfaceScenario]);
		}						
	}
	
	/**
	 *  Test de l'ppartenance d'une surfaces et de la zone au même scénario
	 * @param isoId
	 * @param isoScn
	 * @param surfaceId
	 * @param surfaceScn
	 */
	private void validateScenario(String isoId, String isoScn, String surfaceId, String surfaceScn) {
		
		if(isoId == null || isoScn == null || surfaceId == null|| surfaceScn == null) {
			log.debug(MARKER, "Attenttion un des éléments des tables N_prefixTri_ISO_HT_suffixIsoHt_S_ddd ou N_prefixTri_INONDABLE_suffixInond_S_ddd est null");
			return;
		}
		
		// Si les scénarios ne correspondent pas
		if(!isoScn.equals(surfaceScn)) {			
			context.report(context.createError(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO)
					.setMessageParam("ID", isoId)
					.setMessageParam("TABLE_NAME", "ISO_HT_S")
					.setMessageParam("VALUE_SCENARIO", isoScn)
					.setMessageParam("ID_S_INOND", surfaceId)
					.setMessageParam("EXPECTED_SCENARIO", surfaceScn)
			);			
		}
		
	}

}
