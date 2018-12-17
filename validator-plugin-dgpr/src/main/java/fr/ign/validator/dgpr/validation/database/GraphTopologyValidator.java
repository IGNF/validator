package fr.ign.validator.dgpr.validation.database;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.RowIterator;
import fr.ign.validator.dgpr.database.DatabaseUtils;
import fr.ign.validator.dgpr.database.model.IsoHauteur;
import fr.ign.validator.dgpr.database.model.SurfaceInondable;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.validation.Validator;

public class GraphTopologyValidator implements Validator<Database> {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("TopologicalGraphValidator");

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
	 * Iso classe de hauteur et débit respectent une topologie de graphe 
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
	
	
	public double getTolerance() {
		return 1;
	}


	private void runValidation() throws Exception {
		// Récupération de la table N_prefixTri_INONDABLE_suffixInond_S_ddd
		RowIterator inondTable = database.query(
				"SELECT * FROM N_prefixTri_INONDABLE_suffixInond_S_ddd "
		);

		// Index du champ "ID_S_INOND"
		int indexId = inondTable.getColumn("ID_S_INOND");
		int indexWkt = inondTable.getColumn("WKT");

		if(indexId == -1 || indexWkt == -1) {		
			log.warn(MARKER, "[Error_model] WKT or/and ID_S_INOND column is/are missing.");
			return;
		}

		// Récupération de tous les identifiants + wkt des éléments de la table N_prefixTri_INONDABLE_suffixInond_S_ddd
		ArrayList<SurfaceInondable> surfInondList = new ArrayList<SurfaceInondable>();
		while (inondTable.hasNext()) {
			String[] row = inondTable.next();
			SurfaceInondable surfInond = new SurfaceInondable(row[indexId], row[indexWkt]);
			surfInondList.add(surfInond);
		}

		//Pour chaque surface inondable, on valide son union avec l'union des zones
		for (SurfaceInondable surface: surfInondList) {
			validateUnion(surface);	
		}
	}


	/**
	 * Validate zone intersect in a given SurfaceInondable
	 * @param surfaceId
	 * @return 
	 * @throws Exception 
	 */
	private void validateUnion(SurfaceInondable surface) throws Exception {
		// select zone iso
		RowIterator isoHtTable = database.query(
				" SELECT * FROM N_prefixTri_ISO_HT_suffixIsoHt_S_ddd "
				+ " WHERE ID_S_INOND LIKE '" + surface.getId() + "'"
		);

		// verifier que la geometrie de la surface inondable est valide
		Geometry surfaceInondGeom = format.read(surface.getWkt());
		if(!surfaceInondGeom.isValid()) {
			log.error(MARKER, "Geometry not valid for surfaceInond {}", surface.getId());
			return;
		}
		surface.setGeometry(surfaceInondGeom);

		int indexIdZone = isoHtTable.getColumn("ID_ZONE");
		int indexIdSInond = isoHtTable.getColumn("ID_S_INOND");
		int indexWkt = isoHtTable.getColumn("WKT");

		if(indexIdZone == -1 || indexIdSInond == -1 || indexWkt == -1) {		
			log.warn(MARKER, "[Error_model] WKT, ID_ZONE or/and ID_S_INOND column is/are missing.");
			return;
		}

		ArrayList<IsoHauteur> listIsoHauteur = new ArrayList<IsoHauteur>();
		while (isoHtTable.hasNext()) {
			String[] row = isoHtTable.next();
			IsoHauteur isoHauteur = new IsoHauteur(row[indexIdZone], row[indexIdSInond], row[indexWkt]);
			listIsoHauteur.add(isoHauteur);	
		}

		if (listIsoHauteur.size() == 0) {
			log.warn(MARKER, "There is no isoHt feature for {} surfaceInond", surface.getId());
			return;
		}
		if (listIsoHauteur.size() == 1) {
			Geometry zhunion = format.read( listIsoHauteur.get(0).getWkt() );	
			// Test union between zhunion and surfaceWkt
			validateComparaison(zhunion, surface, listIsoHauteur);
			return;
		}

		List<Geometry> geometries = new ArrayList<Geometry>();	

		boolean atLeastOneError = false;
		// Tests intersect	
		for (int i = 0; i < listIsoHauteur.size(); i++) {
			IsoHauteur isoHauteur = listIsoHauteur.get(i);
			Geometry prevIso = format.read(isoHauteur.getWkt());
			// all geometries must be valid to create a valid union
			if (!prevIso.isValid()) {
				log.error(MARKER, "Geometry not valid for zoneIso {}", isoHauteur.getId());
				return;
			}
			geometries.add(prevIso);

			if (i == listIsoHauteur.size() - 1) {
				continue;
			}

			for (int j = i + 1; j < listIsoHauteur.size(); j++) {
				IsoHauteur currentIsoHauteur = listIsoHauteur.get(j);
				Geometry currentIso = format.read(currentIsoHauteur.getWkt());
				// all geometries must be valid to create a valid union
				if (!currentIso.isValid()) {
					log.error(MARKER, "Geometry not valid for zoneIso {}", currentIsoHauteur.getId());
					return;
				}
				// Test if two geometries of the same set do not cross each other
				// intersection must be empty or a border
				if((prevIso.intersection(currentIso).getGeometryType().equals("Polygon") 
					&& prevIso.intersection(currentIso).getCoordinate() != null)
					|| prevIso.intersection(currentIso).getGeometryType().equals("MultiPolygon")
				) {
					atLeastOneError = true;
				}
			}

		}

		if (atLeastOneError) {
			context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS)
					.setScope(ErrorScope.FEATURE)
					.setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
					.setAttribute("WKT")
					.setFeatureId(surface.getId())
					.setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(), context.getCoordinateReferenceSystem()))
					.setMessageParam("ID_S_INOND", surface.getId())
					.setMessageParam("LIST_ID_ISO_HT", createErrorMessage(listIsoHauteur))
			);
		}

		// Union de la liste 
		GeometryFactory geometryFactory = new GeometryFactory();
		GeometryCollection geometryCollection = (GeometryCollection) geometryFactory.buildGeometry(geometries);
		Geometry zhunion = geometryCollection.union();

		// Test union between zhunion and surfaceWkt
		validateComparaison(zhunion, surface,listIsoHauteur);

	}

	/**
	 * Validate zone intersect in a given SurfaceInondable
	 * @param zhunion
	 * @param surfaceInondWkt
	 * @return 
	 * @throws Exception  
	 */
	private void validateComparaison(Geometry zhunion, SurfaceInondable surface, ArrayList<IsoHauteur> listIsoHauteur) throws Exception {	

		if(!surface.getGeometry().getGeometryType().equals("Polygon") && !surface.getGeometry().getGeometryType().equals("MultiPolygon")) {
			log.debug(MARKER, "S_INOND should be a Polygon or a MultiPolygon.");
			return;
		}

		// Test correspondance entre l'union des isoHT et la S_INOND
		// tolerance to 1 meters
		if(! topologyEqualsWithTolerance(surface.getGeometry(), zhunion, getTolerance())) {							
			context.report(context.createError(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND)
					.setScope(ErrorScope.FEATURE)
					.setFileModel("N_prefixTri_INONDABLE_suffixInond_S_ddd")
					.setAttribute("WKT")
					.setFeatureId(surface.getId())
					.setFeatureBbox(DatabaseUtils.getEnveloppe(surface.getWkt(), context.getCoordinateReferenceSystem()))
					.setMessageParam("ID_S_INOND", surface.getId())
					.setMessageParam("LIST_ID_ISO_HT", createErrorMessage(listIsoHauteur))
			);

			return;
		}
	}


	/**
	 * Create message HT list error
	 * @param listIsoHauteur
	 * @return
	 */
	private String createErrorMessage(ArrayList<IsoHauteur> listIsoHauteur) {
		String errorMessageListHt = "";
		for (IsoHauteur isoHauteur : listIsoHauteur) {
			errorMessageListHt += isoHauteur.idToString() + " ";
		}
		errorMessageListHt = errorMessageListHt.trim();

		return errorMessageListHt;
	}


	public static boolean topologyEqualsWithTolerance(Geometry a, Geometry b, double tolerance) {
		// same topological geometries
		if (a.equalsTopo(b)) {
			return true;
		}
		// buffer comparison
		Geometry aBuffer = a.buffer(tolerance);
		Geometry bBuffer = b.buffer(tolerance);
		if (! aBuffer.contains(b) || ! bBuffer.contains(a)) {
			return false;
		}
		if (!(aBuffer instanceof Polygon) || !(bBuffer instanceof Polygon)) {
			return false;
		}
		if (((Polygon)aBuffer).getNumInteriorRing() != 0 || ((Polygon)bBuffer).getNumInteriorRing() != 0) {
			return false;
		}
		return true;
	}

}
