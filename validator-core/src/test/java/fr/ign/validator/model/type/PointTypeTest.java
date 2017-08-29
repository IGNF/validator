package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;

public class PointTypeTest extends AbstractTypeTest<Geometry> {

	public PointTypeTest() {
		super(new PointType());
	}
	
	public void testBindEmpty(){
		Geometry geometry = type.bind("GEOMETRYCOLLECTION EMPTY") ;
		assertTrue( geometry.isEmpty() );
	}
	
	public void testBindPoint(){
		Geometry geometry = type.bind("POINT(3.0 4.0)") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	public void testBindFakeMultiPoint(){
		Geometry geometry = type.bind("MULTIPOINT((3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	public void testBindFakeGeometryCollection(){
		Geometry geometry = type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
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
			type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(3.0 4.0))") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
}

