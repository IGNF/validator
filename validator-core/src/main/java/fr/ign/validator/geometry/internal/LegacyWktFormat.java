package fr.ign.validator.geometry.internal;

import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

/**
 * Mainly introduced to keep support for "POINT Z (809848 6322607 8)" which is
 * not supported by {@link WKTReader2} with JTS 1.18.
 *
 * @author MBorne
 *
 */
public class LegacyWktFormat implements GeometryFormat {

    private WKTReader reader;

    public LegacyWktFormat() {
        GeometryFactory gf = JTSFactoryFinder.getGeometryFactory();
        this.reader = new WKTReader(gf);
    }

    @Override
    public Geometry read(String s) throws ParseException {
        return reader.read(s);
    }

}
