package fr.ign.validator.geometry;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Transforms coordinates from sourceCRS to targetCRS
 * @author MBorne
 *
 */
public class ProjectionTransform {

	private MathTransform transform;
	
	public ProjectionTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) throws FactoryException{
		this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
	}
	
	public Geometry transform(Geometry geometry) throws MismatchedDimensionException, TransformException {
		return JTS.transform( geometry, transform);
	}

}
