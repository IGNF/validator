package fr.ign.validator.error;

import java.io.File;
import java.lang.reflect.Field;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.exception.InvalidErrorConfigException;
import fr.ign.validator.tools.ResourceHelper;

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

    @Test(expected = InvalidErrorConfigException.class)
    public void testLoadFileNotFound() {
        ErrorFactory factory = new ErrorFactory();
        factory.loadErrorCodes(new File("not-found.json'"));
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

    /**
     * update VALIDATOR_INFO message using custom error file.
     */
    @Test
    public void testLoadCustomErrors() {
        ErrorFactory errorFactory = new ErrorFactory();

        /*
         * check error after
         */
        {
            ValidatorError error = errorFactory.newError(CoreErrorCodes.VALIDATOR_PROJECTION_INFO);
            Assert.assertEquals(CoreErrorCodes.VALIDATOR_PROJECTION_INFO, error.getCode());
            Assert.assertEquals(ErrorLevel.INFO, error.getLevel());
            Assert.assertEquals(
                "La projection {CODE_PROJECTION} ({URI_PROJECTION}) est utilisée pour la validation",
                error.getMessage()
            );
        }

        /* save initial size */
        int initialSize = errorFactory.getPrototypes().size();

        /* load partial custom config (single message translated in English) */
        errorFactory.loadErrorCodes(
            ResourceHelper.getResourceFile(getClass(), "/custom-errors/config.json")
        );

        Assert.assertEquals(initialSize, errorFactory.getPrototypes().size());
        /*
         * check error after
         */
        {
            ValidatorError error = errorFactory.newError(CoreErrorCodes.VALIDATOR_PROJECTION_INFO);
            Assert.assertEquals(CoreErrorCodes.VALIDATOR_PROJECTION_INFO, error.getCode());
            Assert.assertEquals(ErrorLevel.INFO, error.getLevel());
            Assert.assertEquals(
                "The projection {CODE_PROJECTION} ({URI_PROJECTION}) is configured for the validation.",
                error.getMessage()
            );
        }
    }

}
