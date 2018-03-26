package fr.ign.validator.model;

import java.util.Collection;

import org.junit.Test;

import fr.ign.validator.data.Attribute;
import junit.framework.TestCase;


public class AttributeTypeTest extends TestCase {

	@Test
	public void testByNameString(){
		AttributeType<?> type = AttributeType.forName("String");
		assertNotNull(type);
		assertEquals("String",type.getTypeName()) ;
	}
	
	@Test
	public void testConsistencyForNameGetTypeName(){
		AttributeTypeFactory factory = AttributeTypeFactory.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertEquals(name,type.getTypeName()) ;
		}
	}
	
	
	/**
	 * Ensures each type behaves properly when binding null value
	 */
	@Test
	public void testBindNull(){
		AttributeTypeFactory factory = AttributeTypeFactory.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertNull( type.bind(null) ) ;
		}
	}
	
	/**
	 * Ensures creation of new attribute of null value works
	 */
	@Test
	public void testNewAttributeNull(){
		AttributeTypeFactory factory = AttributeTypeFactory.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			Attribute<?> attribute = type.newAttribute(null);
			assertSame(type,attribute.getType());
			assertNull(attribute.getBindedValue());
		}
	}
	
	/**
	 * Ensures that format for null is NULL
	 */
	@Test
	public void testFormatNull(){
		AttributeTypeFactory factory = AttributeTypeFactory.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertNull( type.format(null) ) ;
		}
	}
	
	
}
