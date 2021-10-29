package fr.ign.validator.geometry;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import fr.ign.validator.model.Projection;

/**
 *
 * @author cbouche
 *
 */
public class GeometryLength {

    public static double getPerimeter(Geometry geometry, Projection projection) throws MismatchedDimensionException,
        FactoryException, TransformException {
        if (isProjectionInMeters(projection)) {
            return geometry.getLength();
        }

        Geometry wgs84Geom = geometry;
        // Transformed to WGS84 to get determine local projection
        // (used to get latitude and longitude of origin)
        if (!StringUtils.equals(projection.getCode(), "EPSG:4326")) {
            wgs84Geom = getWGS84Projection(geometry, projection);
        }

        Geometry transformed = getLocalProjection(wgs84Geom, projection);

        return transformed.getLength();
    }

    private static Geometry getLocalProjection(Geometry geometry, Projection sourceProjecion) throws FactoryException,
        MismatchedDimensionException, TransformException {

        String format = "PROJCS[\"local transverse mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\",SPHEROID[\"WGS 84\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],PARAMETER[\"latitude_of_origin\",%s],PARAMETER[\"central_meridian\",%s],PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";
        String wkt = String.format(format, geometry.getCoordinates()[0].getY(), geometry.getCoordinates()[0].getX());

        CoordinateReferenceSystem sourceCRS = sourceProjecion.getCRS();
        CoordinateReferenceSystem targetCRS = CRS.parseWKT(wkt);

        MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformed = JTS.transform(geometry, mathTransform);
        return transformed;
    }

    private static Geometry getWGS84Projection(Geometry geometry, Projection projection) throws FactoryException,
        MismatchedDimensionException, TransformException {

        CoordinateReferenceSystem sourceCRS = projection.getCRS();
        CoordinateReferenceSystem targetCRS = ProjectionList.getCRS84().getCRS();

        MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformed = JTS.transform(geometry, mathTransform);
        return transformed;
    }

    private static boolean isProjectionInMeters(Projection projection) {

        if (CRS.getMapProjection(projection.getCRS()) == null) {
            return false;
        }

        String regexpStr = "UNIT\\[\"m\"";
        Pattern pattern = Pattern.compile(regexpStr, Pattern.MULTILINE);
        String wkt = projection.getCRS().toWKT();

        return pattern.matcher(wkt).find();
    }

}
