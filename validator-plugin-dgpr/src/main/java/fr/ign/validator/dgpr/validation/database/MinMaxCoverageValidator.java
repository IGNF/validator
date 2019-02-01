package fr.ign.validator.dgpr.validation.database;

import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.database.model.IsoHauteur;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class MinMaxCoverageValidator implements Validator<Database> {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("MinMaxCoverageValidator");

	private Database database;

	private Context context;

	/**
	 * Validate all Surface Inondable
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


	private void runValidation() throws Exception {
		// select all surface
		RowIterator innondTable = database.query(
			"SELECT * FROM N_prefixTri_INONDABLE_suffixInond_S_ddd "
		);
		// id column index
		int indexId = innondTable.getColumn("ID_S_INOND");
		int indexWkt = innondTable.getColumn("WKT");

		while (innondTable.hasNext()) {
			String[] row = innondTable.next();
			// for each id perform MinMaxCoverage validation
			try {
				validateSurfaceInondable(row[indexId], row[indexWkt]);
			} catch (NumberFormatException e) {
				log.error(MARKER, "NumberFormatException, impossible de valider les plages de valeurs");
			}
		}
		innondTable.close();
	}


	/**
	 * Validate zone iso ht in a given SurfaceInondable
	 * @param surfaceId
	 * @param wkt 
	 * @throws SQLException 
	 */
	private void validateSurfaceInondable(String surfaceId, String wkt) throws Exception {
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
		isoHtTable.close();

		// Si il n'y a q'une iso hauteur dans la liste, pas besoin de tester, on passe à l'itération suivante de la boucle
		if (listIsoHauteur.size() == 0) {
			return;
		}

		String errorMessageListHt = "";
		for (IsoHauteur isoHauteur : listIsoHauteur) {
			errorMessageListHt += isoHauteur.toString() + " ";
		}
		errorMessageListHt = errorMessageListHt.trim();

		// initialisation
		if (!compare(listIsoHauteur.get(0).getHtMin(), "0.00")) {
			reportError(surfaceId, errorMessageListHt, wkt);
			return;
		}

		if (!compare(listIsoHauteur.get(listIsoHauteur.size() - 1).getHtMax(), null)
			&& !compare(listIsoHauteur.get(listIsoHauteur.size() - 1).getHtMax(), "9999.0")
		) {
			reportError(surfaceId, errorMessageListHt, wkt);
			return;
		}

		for (int i = 0; i < listIsoHauteur.size(); i++) {
			IsoHauteur isoHauteur = listIsoHauteur.get(i);
			// test htmin different htmax
			if (compare(isoHauteur.getHtMin(), isoHauteur.getHtMax())) {
				reportError(surfaceId, errorMessageListHt, wkt);
				return;
			}
			if (i == listIsoHauteur.size() - 1) {
				continue;
			}
			// test htmax == htmin[i+1]
			IsoHauteur isoHauteurNext = listIsoHauteur.get(i + 1);
			if (!compare(isoHauteur.getHtMax(), isoHauteurNext.getHtMin())) {
				reportError(surfaceId, errorMessageListHt, wkt);
				return;
			}
		}

	}
	
	
	private void reportError(String surfaceId, String errorMessageListHt, String wkt) throws Exception {
		Envelope envelope = DatabaseUtils.getEnveloppe(wkt, context.getCoordinateReferenceSystem());
		context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
				.setScope(ErrorScope.FEATURE)
				.setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
				.setAttribute("--")
				.setFeatureId(surfaceId)
				.setFeatureBbox(envelope)
				.setMessageParam("ID_SINOND", surfaceId)
				.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
		);
	}


	public boolean compare(String doubleStrA, String doubleStrB) throws NumberFormatException {
		if (doubleStrA == null || doubleStrB == null) {
			return (doubleStrA == null && doubleStrB == null);
		}
		return Double.compare(Double.parseDouble(doubleStrA), Double.parseDouble(doubleStrB)) == 0;
	}

}
