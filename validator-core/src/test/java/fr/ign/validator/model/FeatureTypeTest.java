package fr.ign.validator.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.model.type.StringType;

/**
 * 
 * @author CBouche
 *
 */
public class FeatureTypeTest {
	private FeatureType featureType ;
	private FeatureType featureTypeChild ;
	
	@Before
	public void setUp() throws Exception {

		featureType = new FeatureType() ;
		featureType.setName("PARENT");
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
		featureTypeChild.setName("CHILD");
		featureTypeChild.setParent(featureType);
		
		{
			StringType attribute = new StringType() ;
			attribute.setName("C");
			featureTypeChild.addAttribute(attribute);
		}
	}
	
	
	@Test
	public void testAttributeCount() {
		Assert.assertEquals(2, featureType.getAttributeCount());
	}
	
	@Test
	public void testIndexOf(){
		Assert.assertEquals(-1,featureType.indexOf("UNKNOWN"));
		Assert.assertEquals(0,featureType.indexOf("A"));
		Assert.assertEquals(1,featureType.indexOf("B"));
	}

	@Test
	public void testIndexOfInheritance(){
		Assert.assertEquals(-1,featureTypeChild.indexOf("UNKNOWN"));
		Assert.assertEquals(0,featureTypeChild.indexOf("A"));
		Assert.assertEquals(1,featureTypeChild.indexOf("B"));
		Assert.assertEquals(2, featureTypeChild.indexOf("C"));
	}
	
	@Test
	public void testGetAttributeByPosition(){
		Assert.assertEquals( "A", featureType.getAttribute(0).getName() );
		Assert.assertEquals( "B", featureType.getAttribute(1).getName() );
		{	
			boolean hasThrown = false ;
			try {
				featureType.getAttribute(2) ;
			}catch(IllegalArgumentException e){
				hasThrown = true ;
			}
			Assert.assertTrue( hasThrown );
		}
	}

	@Test
	public void testGetAttributeByPositionInheritance(){
		Assert.assertNull(featureTypeChild.getAttribute("UNKNOWN"));
		Assert.assertEquals( "A", featureTypeChild.getAttribute(0).getName() );
		Assert.assertEquals( "B", featureTypeChild.getAttribute(1).getName() );
		Assert.assertEquals( "C", featureTypeChild.getAttribute(2).getName() );
		
		{	
			boolean hasThrown = false ;
			try {
				featureType.getAttribute(3) ;
			}catch(IllegalArgumentException e){
				hasThrown = true ;
			}
			Assert.assertTrue( hasThrown );
		}
	}
	
	@Test
	public void testGetAttributeByName(){
		Assert.assertNull(featureType.getAttribute("UNKNOWN"));
		Assert.assertEquals( "A", featureType.getAttribute("A").getName() );
		Assert.assertEquals( "B", featureType.getAttribute("B").getName() );
		Assert.assertNull( featureType.getAttribute("C") );
	}

	@Test
	public void testGetAttributeByNameInheritance(){
		Assert.assertNull(featureTypeChild.getAttribute("UNKNOWN"));
		Assert.assertEquals( "A", featureTypeChild.getAttribute("A").getName() );
		Assert.assertEquals( "B", featureTypeChild.getAttribute("B").getName() );
		Assert.assertEquals( "C", featureTypeChild.getAttribute("C").getName() );
	}
	
}
