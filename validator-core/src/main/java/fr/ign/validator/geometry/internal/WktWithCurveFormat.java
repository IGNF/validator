package fr.ign.validator.geometry.internal;

import org.geotools.geometry.jts.CurvedGeometryFactory;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.geometry.jts.WKTReader2;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

/**
 * WKT format based on {@link WKTReader2} from JTS with supports CURVEPOLYGON,
 * COMPOUNDCURVE,...) but not "POINT Z (809848 6322607 8)"
 *
 * @see https://docs.geotools.org/stable/userguide/library/jts/geometry.html#creating-circularstring
 *
 * @author MBorne
 *
 */
public class WktWithCurveFormat implements GeometryFormat {

    private WKTReader2 reader;

    private CurvedGeometryFactory factory;

    public WktWithCurveFormat() {
        this.factory = new CurvedGeometryFactory(
            JTSFactoryFinder.getGeometryFactory(),
            Double.MAX_VALUE
        );
        this.reader = new WKTReader2(factory);
    }

    /**
     * GeometryFactory used to build the geometries
     *
     * @return
     */
    public CurvedGeometryFactory getFactory() {
        return factory;
    }

    @Override
    public Geometry read(String s) throws ParseException {
        return reader.read(s);
    }

}
