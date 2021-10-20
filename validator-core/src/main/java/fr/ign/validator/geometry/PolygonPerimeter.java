package fr.ign.validator.geometry;

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

		if (projection.getCRS().toWKT().matches(regexpStr)) {
			return false;
		}

		return true;
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
