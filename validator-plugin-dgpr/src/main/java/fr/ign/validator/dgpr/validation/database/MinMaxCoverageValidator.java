package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.database.DocumentDatabase;
import fr.ign.validator.dgpr.database.RowIterator;
import fr.ign.validator.dgpr.database.model.IsoHauteur;
import fr.ign.validator.dgpr.error.DgprErrorCodes;

public class MinMaxCoverageValidator {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("MinMaxCoverageValidator");

	private DocumentDatabase database;

	private Context context;

	/**
	 * Validate all Surface Inondable
	 * @param context
	 * @param document
	 * @param database
	 * @throws Exception
	 */
	public void validate(Context context, Document document, DocumentDatabase database) throws Exception {

		this.database = database;
		this.context = context;

		// Récupération de la table N_prefixTri_INONDABLE_suffixInond_S_ddd
		RowIterator innondTable = database.query(
				"SELECT * FROM N_prefixTri_INONDABLE_suffixInond_S_ddd "
				);
		// Index du champ "ID_S_INOND"
		int indexId = innondTable.getColumn("ID_S_INOND");

		// Récupération de tous les identifiants des éléments de la table N_prefixTri_INONDABLE_suffixInond_S_ddd
		ArrayList<String> identifiants = new ArrayList<String>();
		while (innondTable.hasNext()) {
			String[] row = innondTable.next();
			identifiants.add(row[indexId]);
		}

		//Pour chaque surface innondable, on récupère la liste des iso hauteurs
		for (String surfaceId: identifiants) {
			validateSurfaceInondable(surfaceId);
		}			 	 
	}


	/**
	 * Validate zone iso ht in a given SurfaceInondable
	 * @param surfaceId
	 * @throws SQLException 
	 */
	private void validateSurfaceInondable(String surfaceId) throws SQLException {
		// select zone iso
		RowIterator isoHtTable = database.query(
			" SELECT * FROM N_prefixTri_ISO_HT_suffixIsoHt_S_ddd "
					+ " WHERE ID_S_INOND LIKE '" + surfaceId + "'"
					+ " ORDER BY ht_min ASC;"
		);

		int indexIdZone = isoHtTable.getColumn("ID_ZONE");
		int indexIdSInnond = isoHtTable.getColumn("ID_S_INOND");
		int indexHtMin = isoHtTable.getColumn("HT_MIN");
		int indexHtMax = isoHtTable.getColumn("HT_MAX");

		ArrayList<IsoHauteur> listIsoHauteur = new ArrayList<IsoHauteur>();
		while (isoHtTable.hasNext()) {
			String[] row = isoHtTable.next();
			IsoHauteur isoHauteur = new IsoHauteur(row[indexIdZone], row[indexIdSInnond], row[indexHtMin], row[indexHtMax]);
			listIsoHauteur.add(isoHauteur);					 
		}

		// Si il n'y a q'une iso hauteur dans la liste, pas besoin de tester, on passe à l'itération suivante de la boucle
		if (listIsoHauteur.size() < 2) {
			return;
		}

		String errorMessageListHt = "";
		for (IsoHauteur isoHauteur : listIsoHauteur) {
			errorMessageListHt += isoHauteur.toString() + " ";
		}
		errorMessageListHt = errorMessageListHt.trim();

		// initialisation
		if (!compare(listIsoHauteur.get(0).getHtMin(), "0.00")) {
			context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
				.setMessageParam("ID_SINOND", surfaceId)
				.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
			);
			return;
		}

		if (!compare(listIsoHauteur.get(listIsoHauteur.size() - 1).getHtMax(), null)) {
			context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
				.setMessageParam("ID_SINOND", surfaceId)
				.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
			);
			return;
		}

		for (int i = 0; i < listIsoHauteur.size(); i++) {
			IsoHauteur isoHauteur = listIsoHauteur.get(i);
			// test htmin different htmax
			if (compare(isoHauteur.getHtMin(), isoHauteur.getHtMax())) {
				context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
					.setMessageParam("ID_SINOND", surfaceId)
					.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
				);
				return;
			}
			if (i == listIsoHauteur.size() - 1) {
				continue;
			}
			// test htmax == htmin[i+1]
			IsoHauteur isoHauteurNext = listIsoHauteur.get(i + 1);
			if (!compare(isoHauteur.getHtMax(), isoHauteurNext.getHtMin())) {
				context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
					.setMessageParam("ID_SINOND", surfaceId)
					.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
				);
				return;
			}
		}

	}


	private boolean compare(String doubleStrA, String doubleStrB) {
		if (doubleStrA == null || doubleStrB == null) {
			return (doubleStrA == null && doubleStrB == null);
		}
		return Double.compare(Double.parseDouble(doubleStrA), Double.parseDouble(doubleStrB)) == 0;
	}

}
