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
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.database.DocumentDatabase;
import fr.ign.validator.dgpr.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;

public class InclusionValidator {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("InclusionDatabaseValidator");

	/**
	 * WKT Reader Enable projection transform to WKT Geometries
	 */
	public static WKTReader format = new WKTReader();

	public void validate(Context context, Document document, DocumentDatabase database) throws Exception {
		// String[] header = database.getSchema("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
		// System.out.println(ArrayUtils.toString(header));

		RowIterator unionFaibleIterator = database.query(
				"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
						+ " WHERE scenario LIKE '04Fai'"
				);
		Geometry unionFaible = getUnion2(unionFaibleIterator);

		RowIterator unionMoyIterator = database.query(
				"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
						+ " WHERE scenario LIKE '02Moy'"
				);
		Geometry unionMoy = getUnion2(unionMoyIterator);


		// MOY -> FAIBLE ?
		{
			RowIterator surfMoyIterator = database.query(
					"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
							+ " WHERE scenario LIKE '02Moy'"
					);
			int wktIndex = surfMoyIterator.getColumn("WKT");
			int idIndex = surfMoyIterator.getColumn("ID_S_INOND");
			while (surfMoyIterator.hasNext()) {
				String[] row = surfMoyIterator.next();
				String wkt = row[wktIndex];

				Geometry geometry = format.read(wkt);
				if (unionFaible.contains(geometry)) {
					// all is ok
					System.out.println(row[idIndex] + " est inclu dans 04Fai");
				} else {
					System.out.println(row[idIndex] + " n'est pas inclu dans 04Fai");
					context.report(context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
							.setFeatureId(row[idIndex])
							.setMessageParam("ID_S_INOND", row[idIndex])
							.setMessageParam("SCENARIO_VALUE_FORT", "02Moy")
							.setMessageParam("SCENARIO_VALUE_FAIBLE", "04Fai")
					);
				}
			}
			surfMoyIterator.close();
		}



		// FORT -> FAIBLE && FORT -> MOY
		{
			RowIterator surfForIterator = database.query(
					"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
							+ " WHERE scenario LIKE '01For'"
					);
			int wktIndex = surfForIterator.getColumn("WKT");
			int idIndex = surfForIterator.getColumn("ID_S_INOND");
			while (surfForIterator.hasNext()) {
				String[] row = surfForIterator.next();
				String wkt = row[wktIndex];

				Geometry geometry = format.read(wkt);
				if (unionFaible.contains(geometry)) {
					// all is ok
					System.out.println(row[idIndex] + " est inclu dans 04Fai");
				} else {
					System.out.println(row[idIndex] + " n'est pas inclu dans 04Fai");
					context.report(context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
							.setFeatureId(row[idIndex])
							.setMessageParam("ID_S_INOND", row[idIndex])
							.setMessageParam("SCENARIO_VALUE_FORT", "01For")
							.setMessageParam("SCENARIO_VALUE_FAIBLE", "04Fai")
					);
				}
				if (unionMoy.contains(geometry)) {
					// all is ok
					System.out.println(row[idIndex] + " est inclu dans 02Moy");
				} else {
					System.out.println(row[idIndex] + " n'est pas inclu dans 02Moy");
					context.report(context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
							.setFeatureId(row[idIndex])
							.setMessageParam("ID_S_INOND", row[idIndex])
							.setMessageParam("SCENARIO_VALUE_FORT", "01For")
							.setMessageParam("SCENARIO_VALUE_FAIBLE", "02Moy")
					);
				}
			}
			surfForIterator.close();
		}


	}


	public Geometry getUnion(RowIterator rowIterator) throws Exception {
		Geometry union = null;
		int wktIndex = rowIterator.getColumn("WKT");
		int idIndex = rowIterator.getColumn("ID_S_INOND");
		while (rowIterator.hasNext()) {
			String[] row = rowIterator.next();
			String wkt = row[wktIndex];
			Geometry geometry = format.read(wkt);
			if (union == null) {
				union = geometry;
				System.out.println("début union de " + row[idIndex]);
			} else {
				geometry.union(union);
				System.out.println("avec " + row[idIndex]);
			}
		}
		rowIterator.close();
		System.out.println("fin union");
		return union;
	}
	
	public Geometry getUnion2(RowIterator rowIterator) throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory();
	    List<Geometry> geometries = new ArrayList<Geometry>();
	    
	    int wktIndex = rowIterator.getColumn("WKT");
		int idIndex = rowIterator.getColumn("ID_S_INOND");
		while (rowIterator.hasNext()) {
			String[] row = rowIterator.next();
			String wkt = row[wktIndex];
			Geometry geometry = format.read(wkt);
			geometries.add(geometry);
		}
		rowIterator.close();
	    GeometryCollection geometryCollection = (GeometryCollection) geometryFactory.buildGeometry(geometries);

		return geometryCollection.union();
	}


}
