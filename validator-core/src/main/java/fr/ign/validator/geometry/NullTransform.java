package fr.ign.validator.geometry;

import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.exception.GeometryTransformException;

/**
 * Returns the same geometry (allows to easily disable transforms)
 *
 * @author MBorne
 *
 */
public class NullTransform implements GeometryTransform {

    @Override
    public Geometry transform(Geometry geometry) throws GeometryTransformException {
        return geometry;
    }

}
