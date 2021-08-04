package fr.ign.validator.geometry.internal;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

/**
 * Base class for supported geometry formats.
 * 
 * @author MBorne
 *
 */
public interface GeometryFormat {

    /**
     * Read a geometry from a given string.
     * 
     * @param s
     * @return
     * @throws ParseException
     */
    public Geometry read(String s) throws ParseException;

}
