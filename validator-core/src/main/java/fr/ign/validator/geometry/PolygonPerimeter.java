package fr.ign.validator.geometry;

import java.util.regex.Pattern;

import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.model.Projection;

/**
 *
 * @author cbouche
 *
 */
public class PolygonPerimeter {


	public static boolean isProjectionInMeters(Projection projection) {

		if (CRS.getMapProjection(projection.getCRS()) == null) {
			return false;
		}

        String regexpStr = "UNIT\\[\"m\"";
        Pattern pattern = Pattern.compile(regexpStr, Pattern.MULTILINE);
        String wkt = projection.getCRS().toWKT();

		return pattern.matcher(wkt).find();
	}


	public static double getPerimeter(Geometry geometry) {

		return geometry.getLength();
	}


	public static double getPerimeter(Geometry geometry, Projection geometryProjection, Projection metricProjection) {

        ProjectionTransform projectionTransform = new ProjectionTransform(
        		geometryProjection,
        		metricProjection
		);
        Geometry result = projectionTransform.transform(geometry);

		return result.getLength();
	}

}
