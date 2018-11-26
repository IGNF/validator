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
import fr.ign.validator.dgpr.database.DatabaseUtils;
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

		RowIterator unionFaibleIterator = database.query(
			"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
			+ " WHERE scenario LIKE '04Fai'"
		);
		Geometry unionFaible = DatabaseUtils.getUnion(unionFaibleIterator);

		RowIterator unionMoyIterator = database.query(
			"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
			+ " WHERE scenario LIKE '02Moy'"
		);
		Geometry unionMoy = DatabaseUtils.getUnion(unionMoyIterator);


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
				} else {
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
				} else {
					context.report(context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
							.setFeatureId(row[idIndex])
							.setMessageParam("ID_S_INOND", row[idIndex])
							.setMessageParam("SCENARIO_VALUE_FORT", "01For")
							.setMessageParam("SCENARIO_VALUE_FAIBLE", "04Fai")
					);
				}
				if (unionMoy.contains(geometry)) {
					// all is ok
				} else {
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

}
