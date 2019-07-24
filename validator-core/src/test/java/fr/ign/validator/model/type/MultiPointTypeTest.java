package fr.ign.validator.model.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPoint;


public class MultiPointTypeTest extends AbstractTypeTest<Geometry> {

	public MultiPointTypeTest() {
		super(new MultiPointType());
	}

	@Test
	public void testBindEmpty(){
		Geometry geometry = type.bind("GEOMETRYCOLLECTION EMPTY") ;
		assertTrue( geometry.isEmpty() );
	}

	@Test
	public void testBindPoint(){
		Geometry geometry = type.bind("POINT(3.0 4.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof MultiPoint);
		assertEquals(1,geometry.getNumGeometries());
	}

	@Test
	public void testBindMultiPoint(){
		Geometry geometry = type.bind("MULTIPOINT(3.0 4.0,5.0 6.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof MultiPoint);
		assertEquals(2,geometry.getNumGeometries());
	}

//	@Ignore("improve collection normalization to support this")
//	public void testBindFakeGeometryCollection(){
//		Geometry geometry = type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(5.0 6.0))") ;
//		assertFalse( geometry.isEmpty() );
//		assertTrue(geometry instanceof MultiPoint);
//		assertEquals(2,geometry.getNumGeometries());
//	}


	@Test
	public void testBindLineString(){
		boolean throwException = false ;
		try {
			type.bind("LINESTRING(2.0 3.0,4.0 5.0)") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
	
	@Test
	public void testBindRealGeometryCollection(){
		boolean throwException = false ;
		try {
			type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),LINESTRING(3.0 4.0,5.0 6.0))") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
}

