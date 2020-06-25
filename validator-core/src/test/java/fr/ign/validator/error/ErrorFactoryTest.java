package fr.ign.validator.error;

import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Test ErrorFactory according to error codes
 * 
 * @author MBorne
 *
 */
public class ErrorFactoryTest {

    @Test
    public void testNewFromResourceFactory() {
        ErrorFactory factory = new ErrorFactory();
        Assert.assertFalse(factory.getPrototypes().isEmpty());
    }

    @Test
    public void testAllCodeExists() {
        ErrorFactory factory = new ErrorFactory();

        Field[] fields = CoreErrorCodes.class.getDeclaredFields();
        Assert.assertTrue(fields.length > 20);
        for (Field field : fields) {
            /*
             * Filter on uppercase fields
             */
            if (!field.getName().equals(field.getName().toUpperCase())) {
                continue;
            }
            ErrorCode code = ErrorCode.valueOf(field.getName());
            Assert.assertEquals(code.toString(), field.getName());
            try {
                ValidatorError error = factory.newError(code);
                Assert.assertNotNull(error);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
        }
    }

    @Test
    public void testDefaultConstructor() {
        ErrorFactory errorFactory = new ErrorFactory();

        ValidatorError error = errorFactory.newError(CoreErrorCodes.VALIDATOR_INFO);
        Assert.assertEquals(CoreErrorCodes.VALIDATOR_INFO, error.getCode());
        Assert.assertEquals(ErrorLevel.INFO, error.getLevel());
        Assert.assertEquals("{MESSAGE}", error.getMessage());
    }

}
