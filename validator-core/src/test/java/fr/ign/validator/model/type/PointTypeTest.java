package fr.ign.validator.model.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

public class PointTypeTest extends AbstractTypeTest<Geometry> {

	public PointTypeTest() {
		super(new PointType());
	}

	@Test
	public void testBindEmpty() {
		Geometry geometry = type.bind("GEOMETRYCOLLECTION EMPTY");
		assertTrue(geometry.isEmpty());
	}

	@Test
	public void testBindPoint() {
		Geometry geometry = type.bind("POINT(3.0 4.0)");
		assertFalse(geometry.isEmpty());
		assertTrue(geometry instanceof Point);
	}

	@Test
	public void testBindPointZ() {
		Geometry geometry = type.bind("POINT Z (809848 6322607 8)");
		assertFalse(geometry.isEmpty());
		assertTrue(geometry instanceof Point);
		Point p = (Point) geometry;
		assertEquals("(809848.0, 6322607.0, 8.0)", p.getCoordinate().toString());		
	}

	@Test
	public void testBindFakeMultiPoint() {
		Geometry geometry = type.bind("MULTIPOINT((3.0 4.0))");
		assertFalse(geometry.isEmpty());
		assertTrue(geometry instanceof Point);
	}

	@Test
	public void testBindFakeGeometryCollection() {
		Geometry geometry = type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0))");
		assertFalse(geometry.isEmpty());
		assertTrue(geometry instanceof Point);
	}

	@Test
	public void testBindLineString() {
		boolean throwException = false;
		try {
			type.bind("LINESTRING(2.0 3.0,4.0 5.0)");
		} catch (IllegalArgumentException e) {
			throwException = true;
		}
		assertTrue(throwException);
	}

	@Test
	public void testBindRealGeometryCollection() {
		boolean throwException = false;
		try {
			type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(3.0 4.0))");
		} catch (IllegalArgumentException e) {
			throwException = true;
		}
		assertTrue(throwException);
	}
}
