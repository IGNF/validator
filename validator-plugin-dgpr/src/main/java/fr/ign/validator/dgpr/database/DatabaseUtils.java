package fr.ign.validator.dgpr.database;

import java.util.ArrayList;
import java.util.List;

import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.simplify.DouglasPeuckerSimplifier;

import fr.ign.validator.database.RowIterator;
import fr.ign.validator.geometry.ProjectionTransform;

public class DatabaseUtils {

	/**
	 * WKT Reader Enable projection transform to WKT Geometries
	 */
	public static WKTReader format = new WKTReader();

	/**
	 * WKT writer
	 */
	public static WKTWriter formatWriter = new WKTWriter();
	
	public static GeometryFactory factory = new GeometryFactory();


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
	 * Perform unions of Geometry from two geometries
	 * @param a
	 * @param b
	 * @return
	 */
	public static Geometry getUnion(Geometry a, Geometry b) {
		List<Geometry> geometries = new ArrayList<Geometry>();
		geometries.add(a);
		geometries.add(b);

		GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(geometries);
		return geometryCollection.union();
	}


	/**
	 * Perform unions of Geometry from a database result set
	 * Feature must contains 'WKT' columns
	 * @param rowIterator
	 * @return
	 * @throws Exception
	 */
	public static Geometry getUnion(RowIterator rowIterator) throws Exception {
		List<Geometry> geometries = new ArrayList<Geometry>();

		int wktIndex = rowIterator.getColumn("WKT");
		if (wktIndex == -1) {
			throw new Error("Can't find 'WKT' column");
		}
		while (rowIterator.hasNext()) {
			String[] row = rowIterator.next();
			String wkt = row[wktIndex];
			Geometry geometry = format.read(wkt);
			if (!isValid(geometry)) {
				// TODO throw error ?
				return null;
			}
			geometries.add(geometry);
		}
		rowIterator.close();
		
		GeometryCollection geometryCollection = (GeometryCollection) factory.buildGeometry(geometries);
		return geometryCollection.union();
	}


	public static List<String> getInvalidGeometries(RowIterator rowIterator, String columnName) throws Exception {
		List<String> idGeometries = new ArrayList<String>();

		int wktIndex = rowIterator.getColumn("WKT");
		int idIndex = rowIterator.getColumn(columnName);
		if (wktIndex == -1) {
			throw new Error("Can't find 'WKT' column");
		}
		while (rowIterator.hasNext()) {
			String[] row = rowIterator.next();
			String wkt = row[wktIndex];
			Geometry geometry = format.read(wkt);
			if (!isValid(geometry)) {
				idGeometries.add(row[idIndex]);
			}
		}
		rowIterator.close();

		return idGeometries;
	}


	/**
	 * Return enveloppe of giving Wkt Geometry
	 * @param wkt
	 * @param crs
	 * @return
	 * @throws Exception
	 */
	public static Envelope getEnveloppe(String wkt, CoordinateReferenceSystem crs) throws Exception {
		// depends on geometry
		Geometry geom = new ProjectionTransform(crs).transformWKT(wkt);
		return geom.getEnvelopeInternal();
	}

	
	/**
	 * Identify invalid geometry on which union and intersect operation will failed
	 * @param geometry
	 * @return
	 */
	public static boolean isValid(Geometry geometry) {
		return geometry.isValid();
	}

	
	/**
	 * Identify invalid geometry on which union and intersect operation will failed
	 * @param geometry
	 * @return
	 */
	public static boolean isValidWKT(String wkt) {
		try {
			Geometry geometry = format.read(wkt);
			// Valid geometry ?
			return geometry.isValid();
		} catch (ParseException e) {
			// Invalid WKT format
			return false;
		}
	}


	public static Geometry getGeometryFromWkt(String wkt) {
		try {
			Geometry geometry = format.read(wkt);
			return geometry;
		} catch (ParseException e) {
			// Invalid WKT format
			return null;
		}
	}


	public static Geometry getGeometryFromWkt(String wkt, double tolerance) {
		try {
			Geometry simpleGeom = DouglasPeuckerSimplifier.simplify(format.read(wkt), tolerance);
			return simpleGeom;
		} catch (ParseException e) {
			// Invalid WKT format
			return null;
		}
	}


	public static String getWktFromGeometry(Geometry geom) {
		String wkt = formatWriter.write(geom);
		return wkt;
	}

}
