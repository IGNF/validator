package fr.ign.validator.model;

import org.junit.Test;

import fr.ign.validator.model.type.StringType;
import junit.framework.TestCase;

/**
 * 
 * @author CBouche
 *
 */
public class FeatureTypeTest extends TestCase {
	private FeatureType featureType ;
	private FeatureType featureTypeChild ;
	
	@Override
	protected void setUp() throws Exception {

		featureType = new FeatureType() ;
		featureType.setTypeName("PARENT");
		{
			StringType attribute = new StringType() ;
			attribute.setName("A");
			featureType.addAttribute(attribute);
		}
		{
			StringType attribute = new StringType() ;
			attribute.setName("B");
			featureType.addAttribute(attribute);
		}
	
		featureTypeChild = new FeatureType() ;
		featureTypeChild.setTypeName("CHILD");
		featureTypeChild.setParent(featureType);
		
		{
			StringType attribute = new StringType() ;
			attribute.setName("C");
			featureTypeChild.addAttribute(attribute);
		}
	}
	
	
	@Override
	protected void tearDown() throws Exception {
		featureType = null ;
	}
	
	
	@Test
	public void testAttributeCount() {
		assertEquals(2, featureType.getAttributeCount());
	}
	
	@Test
	public void testIndexOf(){
		assertEquals(-1,featureType.indexOf("UNKNOWN"));
		assertEquals(0,featureType.indexOf("A"));
		assertEquals(1,featureType.indexOf("B"));
	}

	@Test
	public void testIndexOfInheritance(){
		assertEquals(-1,featureTypeChild.indexOf("UNKNOWN"));
		assertEquals(0,featureTypeChild.indexOf("A"));
		assertEquals(1,featureTypeChild.indexOf("B"));
		assertEquals(2, featureTypeChild.indexOf("C"));
	}
	
	@Test
	public void testGetAttributeByPosition(){
		assertEquals( "A", featureType.getAttribute(0).getName() );
		assertEquals( "B", featureType.getAttribute(1).getName() );
		{	
			boolean hasThrown = false ;
			try {
				featureType.getAttribute(2) ;
			}catch(IllegalArgumentException e){
				hasThrown = true ;
			}
			assertTrue( hasThrown );
		}
	}
	@Test
	public void testGetAttributeByPositionInheritance(){
		assertNull(featureTypeChild.getAttribute("UNKNOWN"));
		assertEquals( "A", featureTypeChild.getAttribute(0).getName() );
		assertEquals( "B", featureTypeChild.getAttribute(1).getName() );
		assertEquals( "C", featureTypeChild.getAttribute(2).getName() );
		
		{	
			boolean hasThrown = false ;
			try {
				featureType.getAttribute(3) ;
			}catch(IllegalArgumentException e){
				hasThrown = true ;
			}
			assertTrue( hasThrown );
		}
	}
	
	@Test
	public void testGetAttributeByName(){
		assertNull(featureType.getAttribute("UNKNOWN"));
		assertEquals( "A", featureType.getAttribute("A").getName() );
		assertEquals( "B", featureType.getAttribute("B").getName() );
		assertNull( featureType.getAttribute("C") );
	}
	@Test
	public void testGetAttributeByNameInheritance(){
		assertNull(featureTypeChild.getAttribute("UNKNOWN"));
		assertEquals( "A", featureTypeChild.getAttribute("A").getName() );
		assertEquals( "B", featureTypeChild.getAttribute("B").getName() );
		assertEquals( "C", featureTypeChild.getAttribute("C").getName() );
	}
	
}
