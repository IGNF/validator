package fr.ign.validator.mapping;

import org.junit.Test;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.BooleanType;
import junit.framework.TestCase;

/**
 * @author MBorne
 *
 */
public class FeatureTypeMapperTest extends TestCase {

	@Test
	public void testOneToOne(){
		FeatureType featureType = new FeatureType() ;
		{
			AttributeType<?> attribute = new BooleanType() ;
			attribute.setName("A");
			featureType.addAttribute(attribute);			
		}
		String[] header = new String[]{"A"};
		
		FeatureTypeMapper mapper = FeatureTypeMapper.createMapper(header, featureType) ;
		assertTrue(mapper.getMissingAttributes().isEmpty());
		assertTrue(mapper.getUnexpectedAttributes().isEmpty());
	}
	
	@Test
	public void testManyToMany(){
		FeatureType featureType = new FeatureType() ;
		{
			AttributeType<?> attribute = new BooleanType() ;
			attribute.setName("A");
			featureType.addAttribute(attribute);			
		}
		{
			AttributeType<?> attribute = new BooleanType() ;
			attribute.setName("B");
			featureType.addAttribute(attribute);			
		}
		String[] header = new String[]{"A","C"};
		
		FeatureTypeMapper mapper = FeatureTypeMapper.createMapper(header, featureType) ;
		
		assertEquals(1,mapper.getMissingAttributes().size());
		assertEquals("B",mapper.getMissingAttributes().get(0));
		
		assertEquals(1,mapper.getUnexpectedAttributes().size());
		assertEquals("C",mapper.getUnexpectedAttributes().get(0));
	}
	
	
}
