package fr.ign.validator.geometry;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.geotools.geometry.jts.WKTReader2;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import fr.ign.validator.geometry.internal.GeometryFormat;
import fr.ign.validator.geometry.internal.LegacyWktFormat;
import fr.ign.validator.geometry.internal.WktWithCurveFormat;

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
     * A list of formats to try to read a Geometry with {@link WKTReader},
     * {@link WKTReader2},...
     */
    private List<GeometryFormat> formats = new ArrayList<>(2);

    public GeometryReader() {
        // note that WKTReader doesn't supports curved geometries
        this.formats.add(new WktWithCurveFormat());
        // note that WKTReader2 doesn't support "POINT Z (809848 6322607 8)"
        this.formats.add(new LegacyWktFormat());
        // Note that it might be extended with other formats like GeoJSON, WKB,...
    }

    /**
     * Converts a given string to a JTS {@link Geometry}
     *
     * @param wkt
     * @return
     * @throws ParseException
     */
    public Geometry read(String wkt) throws ParseException {
        if (StringUtils.isEmpty(wkt)) {
            return null;
        }
        for (GeometryFormat format : formats) {
            try {
                return format.read(wkt);
            } catch (ParseException e) {
                // Ignore and try next format
            }
        }
        throw new ParseException(
            String.format(
                "Fail to parse geometry from : %1s", wkt
            )
        );
    }

}
