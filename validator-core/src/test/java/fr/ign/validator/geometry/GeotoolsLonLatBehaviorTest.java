package fr.ign.validator.geometry;

import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * Test geotool behavior with lat,lon order for EPSG codes
 * 
 * @see http://docs.geotools.org/latest/userguide/library/referencing/order.html
 */
public class GeotoolsLonLatBehaviorTest {

	
	@Test
	public void testDefaultBehaviorForEpsg4326() throws Exception {
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326");
		/*
		 *  Should be NORTH_EAST according to GML description
		 *  @see http://www.opengis.net/def/crs/EPSG/0/4326
		 *  "Ellipsoidal 2D CS. Axes: latitude, longitude. Orientations: north, east. UoM: degree"
		 */
		Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
	}

	@Test
	public void testDefaultBehaviorForEpsg2154() throws Exception {
		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:2154");
		/*
		 *  Should be NORTH_EAST according to GML description
		 *  @see http://www.opengis.net/def/crs/EPSG/0/2154
		 *  "This EuroGeographics identifier is for a CRS similar to this but with CS axes in order north, east."
		 */
		Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
	}
	
	// false has no effect...
	
//	@Test
//	public void testNormativeBehaviorForEpsg4326() throws Exception {
//		// disable non standard behavior
//		CRSAuthorityFactory factory = CRS.getAuthorityFactory(false);
//		CoordinateReferenceSystem targetCRS = factory.createCoordinateReferenceSystem("EPSG:4326");
//		Assert.assertEquals(CRS.AxisOrder.NORTH_EAST, CRS.getAxisOrder(targetCRS));
//	}
//	
//	@Test
//	public void testNormativeBehaviorForEpsg4326Bis() throws Exception {
//		// disable non standard behavior (light syntax)
//		CoordinateReferenceSystem targetCRS = CRS.decode("EPSG:4326",false);
//		Assert.assertEquals(CRS.AxisOrder.NORTH_EAST, CRS.getAxisOrder(targetCRS));
//	}
	
	@Test
	public void testDefaultBehaviorForCRS84() throws Exception {
		CoordinateReferenceSystem targetCRS = CRS.decode("CRS:84");
		Assert.assertEquals(CRS.AxisOrder.EAST_NORTH, CRS.getAxisOrder(targetCRS));
	}

}
