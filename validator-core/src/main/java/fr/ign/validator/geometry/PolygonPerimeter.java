package fr.ign.validator.geometry;

import org.locationtech.jts.geom.Geometry;

/**
 * 
 * @author cbouche
 *
 */
public class PolygonPerimeter {


	public static double getPerimeter(Geometry geometry, String srid) {
		return geometry.getLength();
	}

}
