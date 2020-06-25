package fr.ign.validator.error;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * Test ValidatorError behavior about parameters.
 * 
 * @author MBorne
 *
 */
public class ValidatorErrorTest {

    @Test
    public void testSetMessageParam() {
        ValidatorError error = new ValidatorError(CoreErrorCodes.VALIDATOR_INFO);
        error.setMessage("The value '{ACTUAL}' != '{EXPECTED}' (expected)");
        error
            .setMessageParam("ACTUAL", "toto")
            .setMessageParam("EXPECTED", "titi");
        Assert.assertEquals("The value 'toto' != 'titi' (expected)", error.getMessage());
    }

    /**
     * Check that setMessageParam replaces all
     */
    @Test
    public void testSetMessageParamMultiple() {
        ValidatorError error = new ValidatorError(CoreErrorCodes.VALIDATOR_INFO);
        error.setMessage("{SAME} {SAME}");
        error
            .setMessageParam("SAME", "toto");
        Assert.assertEquals("toto toto", error.getMessage());
    }

}
