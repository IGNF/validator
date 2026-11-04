package fr.ign.validator.geometry.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.geometry.GeometryReader;

/**
 * Ensure that {@link FastWktWriter} produces the same output as
 * {@link Geometry#toText()}
 */
public class FastWktWriterTest {

    private GeometryReader reader = new GeometryReader();

    private GeometryFactory factory = new GeometryFactory();

    private static final String[] SUPPORTED = {
        "POINT(1 2)",
        "POINT(-1.5 2.25)",
        "POINT(0.30000000000000004 0.1)",
        "POINT(-0.17213741005674965 3.0000000000000004)",
        "POINT(1e-7 -2.5E-20)",
        "POINT(123456789012345.6 -0.000001)",
        "LINESTRING(0 0,1 1)",
        "POLYGON((0 0,1 0,1 1,0 0))",
        "POLYGON ((0 0, 10 0, 10 10, 0 10, 0 0), (1 1, 2 1, 2 2, 1 1), (5 5, 6 5, 6 6, 5 5))",
        "MULTILINESTRING((0 0,1 1),(2 2,3 3))",
        "MULTIPOLYGON(((0 0,1 0,1 1,0 0)),((2 2,3 2,3 3,2 2)))",
        "MULTIPOLYGON (((0 0, 10 0, 10 10, 0 10, 0 0), (1 1, 2 1, 2 2, 1 1)))",
        "POINT Z (1 2 3)"
    };

    private static final String[] UNSUPPORTED = {
        "POINT EMPTY",
        "POLYGON EMPTY",
        "MULTIPOLYGON EMPTY",
        "MULTIPOINT((1 2),(3 4))",
        "GEOMETRYCOLLECTION(POINT(1 2))",
        "CURVEPOLYGON(CIRCULARSTRING(0 0,1 1,2 0,1 -1,0 0))",
        "POINT(1e16 2)",
        "POINT(1 2e300)"
    };

    @Test
    public void testSupported() throws ParseException {
        for (String wkt : SUPPORTED) {
            Geometry geometry = reader.read(wkt);
            assertNotNull(wkt, FastWktWriter.tryWrite(geometry));
            assertEquals(wkt, geometry.toText(), FastWktWriter.write(geometry));
        }
    }

    @Test
    public void testUnsupportedFallback() throws ParseException {
        for (String wkt : UNSUPPORTED) {
            Geometry geometry = reader.read(wkt);
            assertNull(wkt, FastWktWriter.tryWrite(geometry));
            assertEquals(wkt, geometry.toText(), FastWktWriter.write(geometry));
        }
    }

    @Test
    public void testLinearRingAndFixedPrecisionFallback() {
        Coordinate[] coordinates = {
            new Coordinate(0, 0), new Coordinate(1, 0), new Coordinate(1, 1), new Coordinate(0, 0)
        };
        LinearRing ring = factory.createLinearRing(coordinates);
        assertNull(FastWktWriter.tryWrite(ring));
        assertEquals(ring.toText(), FastWktWriter.write(ring));

        GeometryFactory fixed = new GeometryFactory(new PrecisionModel(1000.0));
        Polygon polygon = fixed.createPolygon(coordinates);
        assertNull(FastWktWriter.tryWrite(polygon));
        assertEquals(polygon.toText(), FastWktWriter.write(polygon));
    }

    @Test
    public void testNegativeZero() {
        Geometry point = factory.createPoint(new Coordinate(-0.0, -1e-20));
        assertEquals(point.toText(), FastWktWriter.write(point));
    }

    @Test
    public void testRandomOrdinates() {
        Random random = new Random(42);
        for (int i = 0; i < 200000; i++) {
            double x = (random.nextDouble() - 0.5) * Math.pow(10, random.nextInt(20) - 5);
            double y = random.nextInt(3) == 0 ? Math.round(random.nextDouble() * 1e9) / 1e3 : random.nextGaussian();
            Geometry point = factory.createPoint(new Coordinate(x, y));
            assertEquals(point.toText(), FastWktWriter.write(point));
        }
    }

    @Test
    public void testRandomMultiPolygons() {
        Random random = new Random(7);
        for (int i = 0; i < 500; i++) {
            Polygon[] polygons = new Polygon[1 + random.nextInt(3)];
            for (int p = 0; p < polygons.length; p++) {
                double cx = random.nextDouble() * 10 - 5;
                double cy = 40 + random.nextDouble() * 10;
                int n = 3 + random.nextInt(50);
                Coordinate[] coordinates = new Coordinate[n + 1];
                for (int k = 0; k < n; k++) {
                    double a = 2 * Math.PI * k / n;
                    coordinates[k] = new Coordinate(cx + Math.cos(a) * 0.01, cy + Math.sin(a) * 0.01);
                }
                coordinates[n] = coordinates[0];
                polygons[p] = factory.createPolygon(coordinates);
            }
            Geometry geometry = factory.createMultiPolygon(polygons);
            assertEquals(geometry.toText(), FastWktWriter.write(geometry));
        }
    }

}
