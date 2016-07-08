package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

import junit.framework.TestCase;

public class PointTypeTest extends TestCase {

	private PointType pointType = new PointType() ;
	
	public void testBindEmpty(){
		Geometry geometry = pointType.bind("GEOMETRYCOLLECTION EMPTY") ;
		assertTrue( geometry.isEmpty() );
	}
	
	public void testBindPoint(){
		Geometry geometry = pointType.bind("POINT(3.0 4.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	public void testBindFakeMultiPoint(){
		Geometry geometry = pointType.bind("MULTIPOINT((3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	public void testBindFakeGeometryCollection(){
		Geometry geometry = pointType.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	public void testBindLineString(){
		boolean throwException = false ;
		try {
			pointType.bind("LINESTRING(2.0 3.0,4.0 5.0)") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
	
	
	public void testBindRealGeometryCollection(){
		boolean throwException = false ;
		try {
			pointType.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(3.0 4.0))") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
}

