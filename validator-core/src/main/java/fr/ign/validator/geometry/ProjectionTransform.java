package fr.ign.validator.geometry;

import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import fr.ign.validator.exception.GeometryTransformException;
import fr.ign.validator.model.Projection;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Transforms coordinates from sourceCRS to targetCRS
 *
 * @author MBorne
 *
 */
public class ProjectionTransform implements GeometryTransform {

    private static final Projection CRS84 = ProjectionList.getCRS84();

    private MathTransform transform;

    /**
     * WKT Reader Enable projection transform to WKT Geometries
     */
    private static WKTReader format = new WKTReader();

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
     * Create a ProjectionTransform from sourceCRS to targetCRS.
     *
     * @param sourceCRS
     * @param targetCRS
     */
    private ProjectionTransform(CoordinateReferenceSystem sourceCRS, CoordinateReferenceSystem targetCRS) {
        try {
            this.transform = CRS.findMathTransform(sourceCRS, targetCRS);
        } catch (FactoryException e) {
            String message = String.format(
                "fail to find transform from '%1s' to '%2s'",
                sourceCRS.getName(),
                targetCRS.getName()
            );
            throw new GeometryTransformException(message, e);
        }
    }

    /**
     * Transform a given geometry from source to target projection.
     *
     * @param geometry
     * @return
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    @Override
    public Geometry transform(Geometry geometry) throws GeometryTransformException {
        try {
            return JTS.transform(geometry, transform);
        } catch (MismatchedDimensionException | TransformException e) {
            throw new GeometryTransformException("fail to apply transform", e);
        }
    }

    /**
     * Transform WKT String in source CRS to Geometry in target CRS
     *
     * @param wkt
     * @return
     * @throws GeometryTransformException
     * @throws ParseException
     */
    @Deprecated
    public Geometry transformWKT(String wkt) throws GeometryTransformException, ParseException {
        Geometry geom = format.read(wkt);
        return transform(geom);
    }

}
