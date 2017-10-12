package fr.ign.validator.cnig.error;

import java.lang.reflect.Field;

import org.junit.Test;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorFactory;
import fr.ign.validator.error.ValidatorError;
import junit.framework.TestCase;

public class CnigErrorCodesTest extends TestCase {

	@Test
	public void testNewFromResourceFactory(){
		ErrorFactory factory = new ErrorFactory() ;
		assertFalse( factory.getPrototypes().isEmpty() ) ;
	}

	public void testAllCodeExists(){
		ErrorFactory factory = new ErrorFactory() ;
		
		Field[] fields = CnigErrorCodes.class.getDeclaredFields() ;
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
