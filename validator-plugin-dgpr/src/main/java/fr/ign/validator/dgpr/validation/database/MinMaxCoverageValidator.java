package fr.ign.validator.dgpr.validation.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.database.model.IsoHauteur;
import fr.ign.validator.dgpr.database.model.SurfaceInondable;
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
				validateSurfaceInondable(new SurfaceInondable(row[indexId], row[indexWkt]));
			} catch (NumberFormatException e) {
				log.error(MARKER, "NumberFormatException, impossible de valider les plages de valeurs");
			}
		}
		innondTable.close();
	}


	/**
	 * Validate zone iso ht in a given SurfaceInondable
	 * @param surfaceInondable
	 * @throws Exception
	 */
	private void validateSurfaceInondable(SurfaceInondable surfaceInondable) throws Exception {
		if (surfaceInondable.getId() == null || surfaceInondable.getId().equals("null")) {		
			log.error(MARKER, "{} - Impossible de valider la couverture de hauteur, identifiant 'null' détecté ");
			return;
		}
		// select zone iso
		RowIterator isoHtTable = database.query(
				" SELECT * FROM N_prefixTri_ISO_HT_suffixIsoHt_S_ddd "
				+ " WHERE ID_S_INOND LIKE '" + surfaceInondable.getId() + "'"
				+ " ORDER BY ht_min ASC ;"
		);

		int indexIdZone = isoHtTable.getColumn("ID_ZONE");
		int indexIdSInnond = isoHtTable.getColumn("ID_S_INOND");
		int indexHtMin = isoHtTable.getColumn("HT_MIN");
		int indexHtMax = isoHtTable.getColumn("HT_MAX");

		ArrayList<IsoHauteur> sortedIsoHauteurs = new ArrayList<IsoHauteur>();
		while (isoHtTable.hasNext()) {
			String[] row = isoHtTable.next();
			IsoHauteur isoHauteur = new IsoHauteur(row[indexIdZone], row[indexIdSInnond], row[indexHtMin], row[indexHtMax]);
			sortedIsoHauteurs.add(isoHauteur);					 
		}
		isoHtTable.close();

		// Si il n'y a q'une iso hauteur dans la liste, pas besoin de tester, on passe à l'itération suivante de la boucle
		if (sortedIsoHauteurs.size() == 0) {
			return;
		}

		if (!evaluate(surfaceInondable, sortedIsoHauteurs)) {
			reportError(surfaceInondable, sortedIsoHauteurs);
		}
	}

	private void reportError(SurfaceInondable surfaceInondable, List<IsoHauteur> isoHauteurs) throws Exception {
		// ids part
		String errorMessageListHt = "";
		for (IsoHauteur isoHauteur : isoHauteurs) {
			errorMessageListHt += isoHauteur.toString() + " ";
		}
		errorMessageListHt = errorMessageListHt.trim();
		// enveloppe part
		Envelope envelope = DatabaseUtils.getEnveloppe(surfaceInondable.getWkt(), context.getCoordinateReferenceSystem());
		// report error
		context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED)
				.setScope(ErrorScope.FEATURE)
				.setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
				.setAttribute("--")
				.setFeatureId(surfaceInondable.getId())
				.setFeatureBbox(envelope)
				.setMessageParam("ID_SINOND", surfaceInondable.getId())
				.setMessageParam("LIST_VALUE_MIN_MAX", errorMessageListHt)
		);
	}
	
	
	/**
	 * compare to str has double
	 * @param doubleStrA
	 * @param doubleStrB
	 * @return
	 * @throws NumberFormatException
	 */
	public boolean compare(String doubleStrA, String doubleStrB) throws NumberFormatException {
		if (doubleStrA == null || doubleStrB == null) {
			return (doubleStrA == null && doubleStrB == null);
		}
		return Double.compare(Double.parseDouble(doubleStrA), Double.parseDouble(doubleStrB)) == 0;
	}


	/**
	 * 
	 * @param surfaceInondable
	 * @param isoHauteurs must be sorted (by asc htmin)
	 * @return
	 */
	public boolean evaluate(SurfaceInondable surfaceInondable, List<IsoHauteur> isoHauteurs) {
		// initialisation
		if (!compare(isoHauteurs.get(0).getHtMin(), "0.00")) {
			return false;
		}

		for (int i = 0; i < isoHauteurs.size(); i++) {
			IsoHauteur isoHauteur = isoHauteurs.get(i);
			// 1) htmax != null && htmin != null
			if (isoHauteur.getHtMax() == null && isoHauteur.getHtMin() == null) {
				return false;
			}
			// 2) htmax === null || htmin[i] > htmax[i]
			if (isoHauteur.getHtMax() != null &&
				Double.compare(Double.parseDouble(isoHauteur.getHtMax()), Double.parseDouble(isoHauteur.getHtMin())) <= 0
			) {
				return false;
			}
			// if last -> continue
			if (i == isoHauteurs.size() - 1) {
				continue;
			}
			IsoHauteur isoHauteurNext = isoHauteurs.get(i + 1);
			// 3) htmin[i] / htmax[i] === hmin[i+1] / htmax[i+1]
			if (compare(isoHauteur.getHtMin(), isoHauteurNext.getHtMin())
				&& compare(isoHauteur.getHtMax(), isoHauteurNext.getHtMax())
			) {
				continue;
			}
			// OR
			// 3bis) htmax[i] === hmin[i+1]
			if (compare(isoHauteur.getHtMax(), isoHauteurNext.getHtMin())) {
				continue;
			}
			return false;
		}

		return true;
	}

}
