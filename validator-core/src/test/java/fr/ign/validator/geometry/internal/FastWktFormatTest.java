package fr.ign.validator.geometry.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Locale;
import java.util.Random;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;

/**
 * Ensure that {@link FastWktFormat} produces the same geometries as
 * {@link WktWithCurveFormat} (WKTReader2) and rejects what it doesn't support.
 */
public class FastWktFormatTest {

    private WktWithCurveFormat reference = new WktWithCurveFormat();

    private FastWktFormat fast = new FastWktFormat(reference.getFactory());

    private static final String[] SUPPORTED = {
        "POINT(1 2)",
        "POINT (1 2)",
        "  POINT ( 1   2 )  ",
        "POINT(-1.5 2.25)",
        "POINT(1e3 -2.5E-2)",
        "POINT(1.5e+3 2)",
        "POINT(1. 2.)",
        "POINT(0.30000000000000004 0.1)",
        "POINT(\t1\n2\r)",
        "LINESTRING(0 0,1 1)",
        "LINESTRING (0 0, 1 1, 2 0)",
        "POLYGON((0 0,1 0,1 1,0 0))",
        "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0), (1 1, 2 1, 2 2, 1 1), (5 5, 6 5, 6 6, 5 5))",
        "MULTILINESTRING((0 0,1 1),(2 2,3 3))",
        "MULTIPOLYGON(((0 0,1 0,1 1,0 0)),((2 2,3 2,3 3,2 2)))",
        "MULTIPOLYGON (((0 0, 10 0, 10 10, 0 10, 0 0), (1 1, 2 1, 2 2, 1 1)))",
        "POLYGON((700000.123456789012 6600000.98765432109876,700100 6600000,700100 6600100,700000.123456789012 6600000.98765432109876))"
    };

    private static final String[] UNSUPPORTED = {
        "",
        "point(1 2)",
        "POINT EMPTY",
        "POINT Z (1 2 3)",
        "POINT(1 2 3)",
        "POINT M (1 2 3)",
        "POINT(+1 2)",
        "POINT(.5 2)",
        "POINT(1,2)",
        "POINT(NaN 2)",
        "POINT(1d 2)",
        "POINT(1 2)x",
        "POINT(1 2",
        "POINT(1e 2)",
        "MULTIPOINT((1 2),(3 4))",
        "MULTIPOINT(1 2,3 4)",
        "GEOMETRYCOLLECTION(POINT(1 2))",
        "LINESTRING EMPTY",
        "POLYGON EMPTY",
        "MULTIPOLYGON EMPTY",
        "CIRCULARSTRING(0 0,1 1,2 0)",
        "CURVEPOLYGON(CIRCULARSTRING(0 0,1 1,2 0,1 -1,0 0))",
        "LINESTRING Z (0 0 0,1 1 1)",
        "MULTIPOLYGON(((0 0,1 0,1 1,0 0)),)"
    };

    @Test
    public void testSupported() throws ParseException {
        for (String wkt : SUPPORTED) {
            assertSameGeometry(wkt, reference.read(wkt), fast.read(wkt));
        }
    }

    @Test
    public void testUnsupported() {
        for (String wkt : UNSUPPORTED) {
            try {
                fast.read(wkt);
                fail("ParseException expected for " + wkt);
            } catch (ParseException e) {
                // expected
            }
        }
    }

    @Test
    public void testIllegalArgumentLikeReference() throws ParseException {
        String[] wkts = {
            "POLYGON((0 0,1 0,1 1,0 1))",
            "LINESTRING(0 0)"
        };
        for (String wkt : wkts) {
            assertIllegalArgument(wkt, reference);
            assertIllegalArgument(wkt, fast);
        }
    }

    @Test
    public void testRandomGeometries() throws ParseException {
        Random random = new Random(42);
        for (int i = 0; i < 2000; i++) {
            String wkt = randomMultiPolygon(random);
            assertSameGeometry(wkt, reference.read(wkt), fast.read(wkt));
        }
    }

    private String randomMultiPolygon(Random random) {
        StringBuilder sb = new StringBuilder("MULTIPOLYGON (");
        int nPolygons = 1 + random.nextInt(3);
        for (int p = 0; p < nPolygons; p++) {
            if (p > 0) {
                sb.append(",");
            }
            sb.append("((");
            int n = 3 + random.nextInt(20);
            String first = null;
            for (int k = 0; k < n; k++) {
                String coordinate = randomNumber(random) + " " + randomNumber(random);
                if (first == null) {
                    first = coordinate;
                }
                sb.append(coordinate).append(",");
            }
            sb.append(first).append("))");
        }
        return sb.append(")").toString();
    }

    private String randomNumber(Random random) {
        double value = (random.nextDouble() - 0.2) * Math.pow(10, random.nextInt(9) - 2);
        switch (random.nextInt(4)) {
        case 0:
            return Double.toString(value);
        case 1:
            return String.format(Locale.ROOT, "%.17g", value).trim();
        case 2:
            return String.format(Locale.ROOT, "%.3f", value);
        default:
            return String.format(Locale.ROOT, "%.10e", value);
        }
    }

    private void assertIllegalArgument(String wkt, GeometryFormat format) throws ParseException {
        try {
            format.read(wkt);
            fail("IllegalArgumentException expected for " + wkt);
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    private void assertSameGeometry(String wkt, Geometry expected, Geometry actual) {
        assertEquals(wkt, expected.getClass(), actual.getClass());
        assertSame(wkt, expected.getFactory().getClass(), actual.getFactory().getClass());
        assertEquals(wkt, expected.getSRID(), actual.getSRID());
        assertEquals(wkt, expected.toText(), actual.toText());
        assertTrue(wkt, expected.equalsExact(actual));
        assertEquals(wkt, expected.getNumGeometries(), actual.getNumGeometries());
        if (expected.getNumGeometries() > 1 || expected != expected.getGeometryN(0)) {
            for (int i = 0; i < expected.getNumGeometries(); i++) {
                assertSameGeometry(wkt, expected.getGeometryN(i), actual.getGeometryN(i));
            }
            return;
        }
        if (expected instanceof Polygon) {
            Polygon e = (Polygon) expected;
            Polygon a = (Polygon) actual;
            assertSameGeometry(wkt, e.getExteriorRing(), a.getExteriorRing());
            assertEquals(wkt, e.getNumInteriorRing(), a.getNumInteriorRing());
            for (int i = 0; i < e.getNumInteriorRing(); i++) {
                assertSameGeometry(wkt, e.getInteriorRingN(i), a.getInteriorRingN(i));
            }
        } else if (expected instanceof LineString || expected instanceof Point) {
            org.locationtech.jts.geom.CoordinateSequence e = expected instanceof Point
                ? ((Point) expected).getCoordinateSequence()
                : ((LineString) expected).getCoordinateSequence();
            org.locationtech.jts.geom.CoordinateSequence a = actual instanceof Point
                ? ((Point) actual).getCoordinateSequence()
                : ((LineString) actual).getCoordinateSequence();
            assertEquals(wkt, e.getClass(), a.getClass());
            assertEquals(wkt, e.getDimension(), a.getDimension());
            assertEquals(wkt, e.getMeasures(), a.getMeasures());
            assertEquals(wkt, e.size(), a.size());
            for (int i = 0; i < e.size(); i++) {
                Coordinate ce = e.getCoordinate(i);
                Coordinate ca = a.getCoordinate(i);
                assertEquals(wkt, ce.getClass(), ca.getClass());
                assertEquals(wkt, Double.doubleToLongBits(ce.getX()), Double.doubleToLongBits(ca.getX()));
                assertEquals(wkt, Double.doubleToLongBits(ce.getY()), Double.doubleToLongBits(ca.getY()));
                assertEquals(wkt, Double.doubleToLongBits(ce.getZ()), Double.doubleToLongBits(ca.getZ()));
            }
        }
    }

}
