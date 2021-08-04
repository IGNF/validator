package fr.ign.validator.geometry;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Helper class to read geometries with a support for curved geometries. Note
 * that it currently supports only WKT format produced by ogr2ogr.
 *
 * @see https://github.com/IGNF/validator/issues/232 related to the introduction
 *      of this class to avoid the direct use of WKTReader from JTS.
 * 
 * @author MBorne
 *
 */
public class GeometryReader {
    /**
     * WKT reader from JTS.
     */
    private WKTReader format;

    public GeometryReader() {
        this.format = new WKTReader();
    }

    public Geometry read(String wkt) throws ParseException {
        return format.read(wkt);
    }

}
