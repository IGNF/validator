package fr.ign.validator.model.type;

import org.junit.Ignore;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;

public class MultiPointTypeTest extends AbstractTypeTest<Geometry> {

	public MultiPointTypeTest() {
		super(new MultiPointType());
	}
	
	public void testBindEmpty(){
		Geometry geometry = type.bind("GEOMETRYCOLLECTION EMPTY") ;
		assertTrue( geometry.isEmpty() );
	}
	
	public void testBindPoint(){
		Geometry geometry = type.bind("POINT(3.0 4.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof MultiPoint);
		assertEquals(1,geometry.getNumGeometries());
	}
	
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

	
	public void testBindLineString(){
		boolean throwException = false ;
		try {
			type.bind("LINESTRING(2.0 3.0,4.0 5.0)") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
	
	
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

