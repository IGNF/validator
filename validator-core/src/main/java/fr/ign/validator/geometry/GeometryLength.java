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
 * GeometryLength
 * Allow you to determine the length in meter of any geometry
 *
 * @author cbouche
 *
 */
public class GeometryLength {

	/**
	 * get perimeter in meter given the source projection
	 * @param geometry
	 * @param projection
	 * @return
	 * @throws MismatchedDimensionException
	 * @throws FactoryException
	 * @throws TransformException
	 */
    public static double getPerimeter(Geometry geometry, Projection projection) throws MismatchedDimensionException,
        FactoryException, TransformException {
        if (isProjectionInMeters(projection)) {
            return geometry.getLength();
        }

        Geometry wgs84Geom = geometry;
        // To create local projection you first need the projection in CRS:84
        if (!StringUtils.equals(projection.getCode(), "CRS:84")) {
            wgs84Geom = getWGS84Projection(geometry, projection);
        }

        Geometry transformed = getLocalProjection(wgs84Geom, projection);

        return transformed.getLength();
    }


    /**
     * get local projection given a CRS:84's geometry 
     * @param geometry
     * @param sourceProjecion
     * @return
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    private static Geometry getLocalProjection(Geometry geometry, Projection sourceProjecion) throws FactoryException,
        MismatchedDimensionException, TransformException {

        String format = "PROJCS[\"local transverse mercator\",GEOGCS[\"WGS 84\",DATUM[\"WGS_1984\","
        		+ "SPHEROID[\"WGS 84\",6378137,298.257223563]],PRIMEM[\"Greenwich\",0],"
        		+ "UNIT[\"degree\",0.0174532925199433]],PROJECTION[\"Transverse_Mercator\"],"
        		+ "PARAMETER[\"latitude_of_origin\",%s],PARAMETER[\"central_meridian\",%s],"
        		+ "PARAMETER[\"scale_factor\",1],PARAMETER[\"false_easting\",0],"
        		+ "PARAMETER[\"false_northing\",0],UNIT[\"Meter\",1]]";

        String wkt = String.format(format, geometry.getCoordinates()[0].getY(), geometry.getCoordinates()[0].getX());
        
        CoordinateReferenceSystem sourceCRS = sourceProjecion.getCRS();
        CoordinateReferenceSystem targetCRS = CRS.parseWKT(wkt);

        MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformed = JTS.transform(geometry, mathTransform);
        return transformed;
    }

    /**
     * get geometry in CRS:84 given the source projection
     * @param geometry
     * @param sourceProjection
     * @return
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    private static Geometry getWGS84Projection(Geometry geometry, Projection sourceProjection) throws FactoryException,
        MismatchedDimensionException, TransformException {

        CoordinateReferenceSystem sourceCRS = sourceProjection.getCRS();
        CoordinateReferenceSystem targetCRS = ProjectionList.getCRS84().getCRS();

        MathTransform mathTransform = CRS.findMathTransform(sourceCRS, targetCRS);
        Geometry transformed = JTS.transform(geometry, mathTransform);
        return transformed;
    }

    /**
     * Ensure given projection is in meter
     * in order to avoir reprojection for french document's in EPSG:2154
     * WARNING : the length will be bad for projection like web mercator
     * @param projection
     * @return
     */
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
