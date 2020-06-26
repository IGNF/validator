package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.type.StringType;

public class IdurbaFormatValidatorTest extends CnigValidatorTestBase {

    @Test
    public void testValid() {
        IdurbaFormatValidator validator = new IdurbaFormatValidator();
        // IdurbaFormatV1 (CNIG 2013 and 2014)
        assertTrue(validator.isValid("25349_20010101"));
        assertTrue(validator.isValid("123456789_20010101"));
        // IdurbaFormatV2 (CNIG 2017)
        assertTrue(validator.isValid("25349_CC_20010101"));
        assertTrue(validator.isValid("123456789_PLUI_20010101"));
    }

    public void testNotValid() {
        IdurbaFormatValidator validator = new IdurbaFormatValidator();
        assertFalse(validator.isValid("something"));
        assertFalse(validator.isValid("25349"));
    }

    @Test
    public void testNotValidReport() {
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "test");
        IdurbaFormatValidator validator = new IdurbaFormatValidator();
        validator.validate(context, attribute);
        assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_IDURBA_INVALID));
    }

    @Test
    public void testValidNoReport() {
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "25349_20140101");
        IdurbaFormatValidator validator = new IdurbaFormatValidator();
        validator.validate(context, attribute);
        assertEquals(0, report.countErrors());
    }

}
