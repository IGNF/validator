package fr.ign.validator.geometry;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

/**
 * Transforms coordinates from sourceCRS to targetCRS
 * 
 * @author MBorne
 *
 */
public class ProjectionTransform {

    private MathTransform transform;

    /**
     * Default Coordinate Reference System Used when target source CRS is not
     * EPSG:4326
     */
    private CoordinateReferenceSystem defaultTargetCRS;

    /**
     * WKT Reader Enable projection transform to WKT Geometries
     */
    public static WKTReader format = new WKTReader();

    public ProjectionTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS)
        throws FactoryException {
        this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
    }

    /**
     * Projection Transform Use the default "CRS:84" CRS as target CRS
     * 
     * @param sourceCRS
     * @throws FactoryException
     */
    public ProjectionTransform(CoordinateReferenceSystem sourceCRS) throws FactoryException {
        this.defaultTargetCRS = CRS.decode("CRS:84");
        this.transform = CRS.findMathTransform(sourceCRS, this.defaultTargetCRS);
    }

    public Geometry transform(Geometry geometry) throws MismatchedDimensionException, TransformException {
        return JTS.transform(geometry, transform);
    }

    /**
     * Transform WKT String in source CRS to Geometry in target CRS
     * 
     * @param wkt
     * @return
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    public Geometry transformWKT(String wkt) throws MismatchedDimensionException, TransformException {
        Geometry geom;
        try {
            geom = format.read(wkt);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Format de géométrie invalide : {}", wkt));
        }
        return JTS.transform(geom, transform);
    }

}
