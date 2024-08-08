package fr.ign.validator.geometry;

import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.exception.GeometryTransformException;

/**
 * Apply a transform on a JTS {@link Geometry}.
 *
 * @author MBorne
 *
 */
public interface GeometryTransform {

    /**
     * Transform a given geometry from source to target projection.
     *
     * @param geometry
     * @return
     */
    public Geometry transform(Geometry geometry) throws GeometryTransformException;

}
