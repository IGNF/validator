package fr.ign.validator.geometry;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.geometry.DirectPosition;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 * Test geotools behavior with lat,lon order for EPSG codes.
 *
 * Note that gt-epsg-hsql provides a different behavior than gt-epsg-wkt
 *
 * @see http://docs.geotools.org/latest/userguide/library/referencing/order.html
 */
public class GeotoolsLonLatBehaviorTest {

    /**
     * Ensure that converting EPSG:4326 to CRS:84 flips coordinate
     */
    @Test
    public void test4326toCRS84() throws Exception {
        CoordinateReferenceSystem sourceCRS = CRS.decode("CRS:84");
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");

        MathTransform transform = CRS.findMathTransform(sourceCRS, targetCRS);
        DirectPosition position = new DirectPosition2D(5.0, 45.0);
        DirectPosition result = new DirectPosition2D();
        transform.transform(position, result);
        Assert.assertEquals(45.0, result.getOrdinate(0), 0.1);
        Assert.assertEquals(5.0, result.getOrdinate(1), 0.1);
    }

    @Test
    public void testDefaultBehaviorForEpsg4326() throws Exception {
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
        /*
         * Should be NORTH_EAST according to GML description
         *
         * @see http://www.opengis.net/def/crs/EPSG/0/4326
         * "Ellipsoidal 2D CS. Axes: latitude, longitude. Orientations: north, east. UoM: degree"
         */
        Assert.assertEquals(CRS.AxisOrder.NORTH_EAST, CRS.getAxisOrder(targetCRS));
    }

    @Test
    public void testDefaultBehaviorForEpsg2154() throws Exception {
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2154");
        /*
         * Should be NORTH_EAST according to GML description
         *
         * @see http://www.opengis.net/def/crs/EPSG/0/2154
         * "This EuroGeographics identifier is for a CRS similar to this but with CS axes in order north, east."
         */
        Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
    }

    /**
     * Ensure that forcing lon,lat lead to EAST_NORTH for "EPSG:4326"
     *
     * @throws Exception
     */
    @Test
    public void testNonNormativeBehaviorForEpsg4326() throws Exception {
        CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326", true);
        Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
    }

    /**
     * Ensure that CRS:84 is lon,lat
     *
     * @throws Exception
     */
    @Test
    public void testDefaultBehaviorForCRS84() throws Exception {
        CoordinateReferenceSystem targetCRS = CRS.decode("CRS:84");
        Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
    }

}
