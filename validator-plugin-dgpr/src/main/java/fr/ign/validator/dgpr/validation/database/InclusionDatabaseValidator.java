package fr.ign.validator.dgpr.validation.database;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.database.DocumentDatabase;
import fr.ign.validator.dgpr.database.RowIterator;
import fr.ign.validator.dgpr.error.DgprErrorCodes;

public class InclusionDatabaseValidator {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("InclusionDatabaseValidator");

	/**
	 * WKT Reader Enable projection transform to WKT Geometries
	 */
	public static WKTReader format = new WKTReader();

	public void validate(Context context, Document document, DocumentDatabase database) throws SQLException, ParseException, IOException {
		// String[] header = database.getSchema("N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD");
		// System.out.println(ArrayUtils.toString(header));

		RowIterator surfaceInondFaibleIterator = database.query(
				"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
				+ " WHERE scenario LIKE '04Fai'"
		);
		Geometry union = null;
		int wktIndex = surfaceInondFaibleIterator.getColumn("WKT");
		while (surfaceInondFaibleIterator.hasNext()) {
			String[] row = surfaceInondFaibleIterator.next();
			String wkt = row[wktIndex];
			Geometry geometry = format.read(wkt);
			if (union == null) {
				union = geometry;
			} else {
				geometry.union(union);
			}
		}
		surfaceInondFaibleIterator.close();

		RowIterator surfaceInondForIterator = database.query(
				"SELECT * FROM N_PREFIXTRI_INONDABLE_SUFFIXINOND_S_DDD "
				+ " WHERE scenario LIKE '01For'"
		);
		int wktIndex2 = surfaceInondForIterator.getColumn("WKT");
		int idIndex = surfaceInondForIterator.getColumn("ID_S_INOND");
		while (surfaceInondForIterator.hasNext()) {
			String[] row = surfaceInondForIterator.next();
			String wkt = row[wktIndex2];

			Geometry geometry = format.read(wkt);
			if (union.contains(geometry)) {
				// all is ok
			} else {
				context.report(context.createError(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
						.setMessageParam("ID_S_INOND", row[idIndex])
						.setMessageParam("SCENARIO_VALUE_FORT", "01For")
						.setMessageParam("SCENARIO_VALUE_FAIBLE", "04Fai")
				);
			}
		}
		surfaceInondForIterator.close();
	}

}
