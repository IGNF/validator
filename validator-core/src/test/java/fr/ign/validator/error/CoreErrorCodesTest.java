package fr.ign.validator.error;

import java.lang.reflect.Field;

import org.junit.Test;

import junit.framework.TestCase;

public class CoreErrorCodesTest extends TestCase {

	@Test
	public void testNewFromResourceFactory(){
		ErrorFactory factory = new ErrorFactory() ;
		assertFalse( factory.getPrototypes().isEmpty() ) ;
	}

	public void testAllCodeExists(){
		ErrorFactory factory = new ErrorFactory() ;
		
		Field[] fields = CoreErrorCodes.class.getDeclaredFields() ;
		assertTrue(fields.length > 20);
		for (Field field : fields) {
			/*
			 * Filter on uppercase fields
			 */
			if ( ! field.getName().equals( field.getName().toUpperCase() ) ){
				continue ;
			}
			ErrorCode code = ErrorCode.valueOf(field.getName()) ;
			assertEquals(code.toString(),field.getName()) ;
			try {
				ValidatorError error = factory.newError(code) ;
				assertNotNull(error);
			}catch (Exception e){
				fail(e.getMessage());
			}
		}
	}

}
