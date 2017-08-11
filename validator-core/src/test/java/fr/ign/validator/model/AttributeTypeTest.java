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
		AttributeTypeRegistry factory = AttributeTypeRegistry.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertEquals(name,type.getTypeName()) ;
		}
	}
	
	
	/**
	 * Vérifie que chaque type se comporte correctement pour le binding de la valeur nulle
	 */
	@Test
	public void testBindNull(){
		AttributeTypeRegistry factory = AttributeTypeRegistry.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertNull( type.bind(null) ) ;
		}
	}
	
	/**
	 * Vérifie que la création d'un nouvel attribut de valeur nulle fonctionne
	 */
	@Test
	public void testNewAttributeNull(){
		AttributeTypeRegistry factory = AttributeTypeRegistry.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			Attribute<?> attribute = type.newAttribute(null);
			assertSame(type,attribute.getType());
			assertNull(attribute.getValue());
		}
	}
	
	/**
	 * Vérifie que format sur un NULL renvoie bien NULL
	 */
	@Test
	public void testFormatNull(){
		AttributeTypeRegistry factory = AttributeTypeRegistry.getInstance() ;
		Collection<String> names = factory.getTypeNames() ;
		assertFalse(names.isEmpty());
		for (String name : names) {
			AttributeType<?> type = factory.createAttributeTypeByName(name) ;
			assertNull( type.format(null) ) ;
		}
	}
	
	
}
