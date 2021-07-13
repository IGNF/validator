package fr.ign.validator.tools;

import java.util.Locale;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import fr.ign.validator.exception.GeometryTransformException;
import fr.ign.validator.geometry.ProjectionTransform;
import fr.ign.validator.model.Projection;

/**
 * Manipulates Bounding Boxes (envelope or bbox)
 * 
 * @author MBorne
 *
 */
public class EnvelopeUtils {

    /**
     * Get bounding box from WKT string.
     * 
     * @param wkt
     * @return
     */
    public static Envelope getEnvelope(String wkt) {
        if (wkt == null || wkt.isEmpty()) {
            return new Envelope();
        }
        try {
            WKTReader reader = new WKTReader();
            org.locationtech.jts.geom.Geometry geometry = reader.read(wkt);
            return geometry.getEnvelopeInternal();
        } catch (ParseException e) {
            return new Envelope();
        }
    }

    /**
     * Get CRS:84 envelope from WKT string with a given projection.
     * 
     * @param wkt
     * @param projection
     * @return
     */
    public static Envelope getEnvelope(String wkt, Projection projection) {
        try {
            Geometry geom = new ProjectionTransform(projection).transformWKT(wkt);
            return geom.getEnvelopeInternal();
        } catch (ParseException e) {
            return new Envelope();
        } catch (GeometryTransformException e) {
            return new Envelope();
        }
    }

    /**
     * Formats bbox as string : "xmin,ymin,xmax,ymax"
     * 
     * @param env
     * @return
     */
    public static String format(Envelope env) {
        if (null == env || isNullEnvelope(env)) {
            return "";
        }
        return formatDouble(env.getMinX())
            + "," + formatDouble(env.getMinY())
            + "," + formatDouble(env.getMaxX())
            + "," + formatDouble(env.getMaxY());
    }

    /**
     * Format double
     * 
     * @param value
     * @return
     */
    public static String formatDouble(double value) {
        return String.format(Locale.ROOT, "%.7f", value);
    }

    /**
     * Indicates if bbox is null
     * 
     * @param env
     * @return
     */
    private static boolean isNullEnvelope(Envelope env) {
        Envelope zero = new Envelope(0.0, 0.0, 0.0, 0.0);
        if (env.isNull()) {
            return true;
        } else if (env.equals(zero)) {
            return true;
        } else {
            return false;
        }
    }

}
