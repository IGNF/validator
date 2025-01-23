package fr.ign.validator.cnig.geometry;

import org.locationtech.jts.geom.Geometry;

/**
 * Geometry helpers to document geometry command.
 *
 * @author DDarras
 *
 */
public class GeometryHelpers {

    public static boolean validateGeometry(Geometry geometry){
        if (! geometry.isValid()){
            // Alternative to ST_MakeValid
            geometry.buffer(0);
        }
        return geometry.isValid();
    }
}