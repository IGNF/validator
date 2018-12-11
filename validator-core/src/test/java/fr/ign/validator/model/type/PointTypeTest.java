package fr.ign.validator.model.type;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;


public class PointTypeTest extends AbstractTypeTest<Geometry> {

	public PointTypeTest() {
		super(new PointType());
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
		assertTrue(geometry instanceof Point);
	}
	
	/**
	 * JTS does not support Z notation before 1.15
	 */
	@Test
	@Ignore
	public void testBindPointZ(){
		Geometry geometry = type.bind("POINT Z (809848.050930752 6322607.71635569 0)");
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}

	@Test
	public void testBindFakeMultiPoint(){
		Geometry geometry = type.bind("MULTIPOINT((3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
	@Test
	public void testBindFakeGeometryCollection(){
		Geometry geometry = type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0))") ;
		assertFalse( geometry.isEmpty() );
		assertTrue(geometry instanceof Point);
	}
	
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
			type.bind("GEOMETRYCOLLECTION(POINT(3.0 4.0),POINT(3.0 4.0))") ;
		}catch (IllegalArgumentException e){
			throwException = true ;
		}
		assertTrue(throwException);
	}
}

