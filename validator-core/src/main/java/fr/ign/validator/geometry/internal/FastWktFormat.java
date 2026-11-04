package fr.ign.validator.geometry.internal;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.io.ParseException;

/**
 * Fast WKT parser for the most common 2D linear geometries (POINT, LINESTRING,
 * POLYGON, MULTILINESTRING and MULTIPOLYGON with XY coordinates, as produced by
 * ogr2ogr with "-dim 2").
 *
 * The WKT readers from JTS and GeoTools rely on {@link java.io.StreamTokenizer}
 * which is slow for large geometries. This parser scans the string directly and
 * builds the geometries with the same {@link GeometryFactory} methods.
 *
 * Any other content (EMPTY, Z/M ordinates, curves, MULTIPOINT,
 * GEOMETRYCOLLECTION, unusual number formats,...) is rejected with a
 * {@link ParseException} so that the next {@link GeometryFormat} is used.
 *
 * Note that the {@link GeometryFactory} may throw
 * {@link IllegalArgumentException} (ex : unclosed ring) as the other readers.
 *
 * @author FCerizay
 *
 */
public class FastWktFormat implements GeometryFormat {

    private GeometryFactory factory;

    public FastWktFormat(GeometryFactory factory) {
        this.factory = factory;
    }

    @Override
    public Geometry read(String s) throws ParseException {
        return new Parser(s, factory).parse();
    }

    /**
     * Single use parser (not thread safe)
     */
    private static class Parser {

        private final String s;

        private final int length;

        private final GeometryFactory factory;

        private int pos = 0;

        Parser(String s, GeometryFactory factory) {
            this.s = s;
            this.length = s.length();
            this.factory = factory;
        }

        Geometry parse() throws ParseException {
            skipWhitespaces();
            String type = readKeyword();
            skipWhitespaces();
            Geometry result;
            switch (type) {
            case "POINT":
                expect('(');
                Coordinate coordinate = readCoordinate();
                expect(')');
                result = factory.createPoint(coordinate);
                break;
            case "LINESTRING":
                result = factory.createLineString(readCoordinates());
                break;
            case "POLYGON":
                result = readPolygon();
                break;
            case "MULTILINESTRING":
                result = readMultiLineString();
                break;
            case "MULTIPOLYGON":
                result = readMultiPolygon();
                break;
            default:
                throw unsupported();
            }
            skipWhitespaces();
            if (pos != length) {
                throw unsupported();
            }
            return result;
        }

        private Polygon readPolygon() throws ParseException {
            expect('(');
            LinearRing shell = factory.createLinearRing(readCoordinates());
            List<LinearRing> holes = new ArrayList<>();
            while (nextIs(',')) {
                holes.add(factory.createLinearRing(readCoordinates()));
            }
            expect(')');
            return factory.createPolygon(shell, holes.toArray(new LinearRing[holes.size()]));
        }

        private Geometry readMultiLineString() throws ParseException {
            expect('(');
            List<LineString> lineStrings = new ArrayList<>();
            do {
                lineStrings.add(factory.createLineString(readCoordinates()));
            } while (nextIs(','));
            expect(')');
            return factory.createMultiLineString(lineStrings.toArray(new LineString[lineStrings.size()]));
        }

        private Geometry readMultiPolygon() throws ParseException {
            expect('(');
            List<Polygon> polygons = new ArrayList<>();
            do {
                polygons.add(readPolygon());
            } while (nextIs(','));
            expect(')');
            return factory.createMultiPolygon(polygons.toArray(new Polygon[polygons.size()]));
        }

        /**
         * Read "(x y, x y,...)"
         */
        private Coordinate[] readCoordinates() throws ParseException {
            expect('(');
            List<Coordinate> coordinates = new ArrayList<>();
            do {
                coordinates.add(readCoordinate());
            } while (nextIs(','));
            expect(')');
            return coordinates.toArray(new Coordinate[coordinates.size()]);
        }

        /**
         * Read "x y" (XY only)
         */
        private Coordinate readCoordinate() throws ParseException {
            skipWhitespaces();
            double x = readNumber();
            if (!skipWhitespaces()) {
                throw unsupported();
            }
            double y = readNumber();
            return new Coordinate(x, y);
        }

        /**
         * Read a number matching -?[0-9]+(\.[0-9]*)?([eE][-+]?[0-9]+)?
         */
        private double readNumber() throws ParseException {
            int start = pos;
            if (pos < length && s.charAt(pos) == '-') {
                pos++;
            }
            if (skipDigits() == 0) {
                throw unsupported();
            }
            if (pos < length && s.charAt(pos) == '.') {
                pos++;
                skipDigits();
            }
            if (pos < length && (s.charAt(pos) == 'e' || s.charAt(pos) == 'E')) {
                pos++;
                if (pos < length && (s.charAt(pos) == '-' || s.charAt(pos) == '+')) {
                    pos++;
                }
                if (skipDigits() == 0) {
                    throw unsupported();
                }
            }
            return Double.parseDouble(s.substring(start, pos));
        }

        private int skipDigits() {
            int start = pos;
            while (pos < length && s.charAt(pos) >= '0' && s.charAt(pos) <= '9') {
                pos++;
            }
            return pos - start;
        }

        /**
         * Read an upper case keyword
         */
        private String readKeyword() throws ParseException {
            int start = pos;
            while (pos < length && s.charAt(pos) >= 'A' && s.charAt(pos) <= 'Z') {
                pos++;
            }
            if (start == pos) {
                throw unsupported();
            }
            return s.substring(start, pos);
        }

        /**
         * Skip whitespaces and the expected character
         */
        private void expect(char c) throws ParseException {
            skipWhitespaces();
            if (pos >= length || s.charAt(pos) != c) {
                throw unsupported();
            }
            pos++;
        }

        /**
         * Skip whitespaces and the given character if it is the next one
         */
        private boolean nextIs(char c) {
            skipWhitespaces();
            if (pos < length && s.charAt(pos) == c) {
                pos++;
                return true;
            }
            return false;
        }

        /**
         * @return true if at least a whitespace is skipped
         */
        private boolean skipWhitespaces() {
            int start = pos;
            while (pos < length) {
                char c = s.charAt(pos);
                if (c != ' ' && c != '\t' && c != '\n' && c != '\r') {
                    break;
                }
                pos++;
            }
            return pos != start;
        }

        private ParseException unsupported() {
            return new ParseException("unsupported by FastWktFormat at position " + pos);
        }
    }

}
