package fr.ign.validator.error;

import java.lang.reflect.Field;

import org.junit.Test;

import junit.framework.TestCase;

public class ErrorFactoryTest extends TestCase {

	@Test
	public void testNewFromResourceFactory(){
		ErrorFactory factory = ErrorFactory.newFromRessource() ;
		assertFalse( factory.getPrototypes().isEmpty() ) ;
	}

	public void testAllCodeExists(){
		ErrorFactory factory = ErrorFactory.newFromRessource() ;
		
		Field[] fields = ErrorCode.class.getDeclaredFields() ;
		for (Field field : fields) {
			// Filtrage sur les champs en majuscules...
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
