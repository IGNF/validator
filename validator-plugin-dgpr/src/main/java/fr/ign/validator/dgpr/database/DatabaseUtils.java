package fr.ign.validator.dgpr.database;

import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

public class DatabaseUtils {

	/**
	 * WKT Reader Enable projection transform to WKT Geometries
	 */
	public static WKTReader format = new WKTReader();


	// NOT WORKING (multipolygon ?)
	/*
	 * public Geometry getUnion__(RowIterator rowIterator) throws Exception {
	 *	Geometry union = null;
	 *	int wktIndex = rowIterator.getColumn("WKT");
	 *	while (rowIterator.hasNext()) {
	 *		String[] row = rowIterator.next();
	 *		String wkt = row[wktIndex];
	 *		Geometry geometry = format.read(wkt);
	 *		if (union == null) {
	 *			union = geometry;
	 *		} else {
	 *			geometry.union(union);
	 *		}
	 *	}
	 *	rowIterator.close();
	 *	return union;
	 * }
	 */


	/**
	 * Perform unions of Geometry from a database result set
	 * Feature must contains 'WKT' columns
	 * @param rowIterator
	 * @return
	 * @throws Exception
	 */
	public static Geometry getUnion(RowIterator rowIterator) throws Exception {
		GeometryFactory geometryFactory = new GeometryFactory();
		List<Geometry> geometries = new ArrayList<Geometry>();

		int wktIndex = rowIterator.getColumn("WKT");
		if (wktIndex == -1) {
			throw new Error("Can't find 'WKT' column");
		}
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
