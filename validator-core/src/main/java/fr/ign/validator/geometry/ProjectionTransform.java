package fr.ign.validator.geometry;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import fr.ign.validator.model.Projection;
import fr.ign.validator.repository.ProjectionRepository;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;

/**
 * Transforms coordinates from sourceCRS to targetCRS
 * 
 * @author MBorne
 *
 */
public class ProjectionTransform {

    private static final Projection CRS84 = ProjectionRepository.getInstance().findByCode("CRS:84");

    private MathTransform transform;

    /**
     * WKT Reader Enable projection transform to WKT Geometries
     */
    public static WKTReader format = new WKTReader();

    /**
     * Create a ProjectionTransform from source projection to target projection.
     * 
     * @param source
     * @param target
     */
    public ProjectionTransform(Projection source, Projection target) {
        this(source.getCRS(), target.getCRS());
    }

    /**
     * Create a ProjectionTransform from source projection to CRS:84 (lon,lat).
     * 
     * @param source
     */
    public ProjectionTransform(Projection source) {
        this(source.getCRS(), CRS84.getCRS());
    }

    /**
     * 
     * Create a ProjectionTransform from sourceCRS to targetCRS.
     * 
     * @deprecated prefer type Projection to CoordinateReferenceSystem.
     * 
     * @param sourceCRS
     * @param targetCRS
     */
    public ProjectionTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
        try {
            this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
        } catch (FactoryException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a ProjectionTransform from sourceCRS to CRS:84
     * 
     * @deprecated prefer type Projection to CoordinateReferenceSystem.
     * 
     * @param sourceCRS
     * @throws FactoryException
     */
    public ProjectionTransform(CoordinateReferenceSystem sourceCRS) throws FactoryException {
        this.transform = CRS.findMathTransform(sourceCRS, CRS84.getCRS());
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
