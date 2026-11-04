package fr.ign.validator.geometry.internal;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;

/**
 * Fast equivalent of {@link Geometry#toText()} for the most common geometries
 * (Point, LineString, Polygon, MultiLineString and MultiPolygon from JTS with a
 * floating precision model).
 *
 * {@link org.locationtech.jts.io.WKTWriter} formats each ordinate with a
 * {@link java.text.DecimalFormat} which is slow for large geometries. This
 * writer produces the same output (XY ordinates rounded to 16 fraction digits
 * using the exact binary value, HALF_EVEN).
 *
 * @author FCerizay
 *
 */
public class FastWktWriter {

    /**
     * Maximum number of fraction digits used by WKTWriter for a floating precision
     * model.
     */
    private static final int MAX_FRACTION_DIGITS = 16;

    /**
     * Above this value, the formatting is delegated to Geometry#toText() (integer
     * part with more than 15 digits)
     */
    private static final double MAX_ABS_VALUE = 1e15;

    private FastWktWriter() {
        // static methods only
    }

    /**
     * Write the geometry as WKT (same as {@link Geometry#toText()})
     *
     * @param geometry
     * @return
     */
    public static String write(Geometry geometry) {
        String result = tryWrite(geometry);
        if (result == null) {
            return geometry.toText();
        }
        return result;
    }

    /**
     * @param geometry
     * @return null if the geometry is not supported
     */
    static String tryWrite(Geometry geometry) {
        if (!isSupported(geometry)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(geometry.getNumPoints() * 40 + 32);
        Class<?> type = geometry.getClass();
        if (type == Point.class) {
            sb.append("POINT ");
            if (!appendSequence(sb, ((Point) geometry).getCoordinateSequence())) {
                return null;
            }
        } else if (type == LineString.class) {
            sb.append("LINESTRING ");
            if (!appendSequence(sb, ((LineString) geometry).getCoordinateSequence())) {
                return null;
            }
        } else if (type == Polygon.class) {
            sb.append("POLYGON ");
            if (!appendPolygon(sb, (Polygon) geometry)) {
                return null;
            }
        } else if (type == MultiLineString.class) {
            sb.append("MULTILINESTRING (");
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                if (!appendSequence(sb, ((LineString) geometry.getGeometryN(i)).getCoordinateSequence())) {
                    return null;
                }
            }
            sb.append(')');
        } else if (type == MultiPolygon.class) {
            sb.append("MULTIPOLYGON (");
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                if (!appendPolygon(sb, (Polygon) geometry.getGeometryN(i))) {
                    return null;
                }
            }
            sb.append(')');
        } else {
            return null;
        }
        return sb.toString();
    }

    /**
     * Bounding box of the geometry read from the WKT produced by
     * {@link #write(Geometry)} without writing and reading it.
     *
     * The ordinates are rounded when they are written. As the rounding preserves
     * the order (x &lt;= y implies round(x) &lt;= round(y)), the bounds of the
     * envelope read from the WKT are the rounded bounds of the envelope.
     *
     * @param geometry
     * @return null if it can't be ensured (unsupported geometry or ordinate, zero
     *         bound for which the sign may depend on the order of the coordinates)
     */
    public static Envelope getWrittenEnvelope(Geometry geometry) {
        if (!isSupported(geometry) || !hasSupportedOrdinates(geometry)) {
            return null;
        }
        Envelope envelope = geometry.getEnvelopeInternal();
        double minX = roundTrip(envelope.getMinX());
        double maxX = roundTrip(envelope.getMaxX());
        double minY = roundTrip(envelope.getMinY());
        double maxY = roundTrip(envelope.getMaxY());
        if (Double.isNaN(minX) || Double.isNaN(maxX) || Double.isNaN(minY) || Double.isNaN(maxY)) {
            return null;
        }
        return new Envelope(minX, maxX, minY, maxY);
    }

    /**
     * Value read from the written ordinate (NaN if not supported or zero)
     */
    private static double roundTrip(double value) {
        StringBuilder sb = new StringBuilder(32);
        if (!appendOrdinate(sb, value)) {
            return Double.NaN;
        }
        double result = Double.parseDouble(sb.toString());
        return result == 0.0 ? Double.NaN : result;
    }

    /**
     * True if all the ordinates can be written by appendOrdinate (otherwise,
     * {@link #write(Geometry)} uses Geometry#toText())
     */
    private static boolean hasSupportedOrdinates(Geometry geometry) {
        for (Coordinate coordinate : geometry.getCoordinates()) {
            if (!isSupportedOrdinate(coordinate.getX()) || !isSupportedOrdinate(coordinate.getY())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isSupportedOrdinate(double value) {
        return !Double.isNaN(value) && !Double.isInfinite(value) && Math.abs(value) < MAX_ABS_VALUE;
    }

    private static boolean isSupported(Geometry geometry) {
        if (geometry == null || geometry.isEmpty()) {
            return false;
        }
        PrecisionModel precisionModel = geometry.getPrecisionModel();
        if (precisionModel.getType() != PrecisionModel.FLOATING) {
            return false;
        }
        Class<?> type = geometry.getClass();
        if (type == MultiPolygon.class || type == MultiLineString.class) {
            Class<?> partType = type == MultiPolygon.class ? Polygon.class : LineString.class;
            for (int i = 0; i < geometry.getNumGeometries(); i++) {
                Geometry part = geometry.getGeometryN(i);
                if (part.getClass() != partType || part.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return type == Point.class || type == LineString.class || type == Polygon.class;
    }

    private static boolean appendPolygon(StringBuilder sb, Polygon polygon) {
        sb.append('(');
        if (!appendSequence(sb, polygon.getExteriorRing().getCoordinateSequence())) {
            return false;
        }
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            sb.append(", ");
            if (!appendSequence(sb, polygon.getInteriorRingN(i).getCoordinateSequence())) {
                return false;
            }
        }
        sb.append(')');
        return true;
    }

    /**
     * Append "(x y, x y,...)"
     */
    private static boolean appendSequence(StringBuilder sb, CoordinateSequence sequence) {
        if (sequence.size() == 0) {
            return false;
        }
        sb.append('(');
        for (int i = 0; i < sequence.size(); i++) {
            if (i != 0) {
                sb.append(", ");
            }
            if (!appendOrdinate(sb, sequence.getX(i))) {
                return false;
            }
            sb.append(' ');
            if (!appendOrdinate(sb, sequence.getY(i))) {
                return false;
            }
        }
        sb.append(')');
        return true;
    }

    /**
     * Same output as {@link org.locationtech.jts.io.OrdinateFormat} with 16
     * fraction digits
     */
    static boolean appendOrdinate(StringBuilder sb, double value) {
        if (!isSupportedOrdinate(value)) {
            return false;
        }
        if (value == 0.0) {
            // DecimalFormat keeps the sign of -0.0
            sb.append(1.0 / value < 0 ? "-0" : "0");
            return true;
        }
        BigDecimal shortest = new BigDecimal(Double.toString(value));
        BigDecimal result = shortest.scale() <= MAX_FRACTION_DIGITS ? shortest
            : new BigDecimal(value).setScale(MAX_FRACTION_DIGITS, RoundingMode.HALF_EVEN);
        result = result.stripTrailingZeros();
        if (result.signum() == 0) {
            // ex : -1e-20 is formatted as "-0"
            sb.append(value < 0 ? "-0" : "0");
            return true;
        }
        sb.append(result.toPlainString());
        return true;
    }

}
