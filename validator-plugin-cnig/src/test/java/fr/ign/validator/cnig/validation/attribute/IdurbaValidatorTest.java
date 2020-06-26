package fr.ign.validator.cnig.validation.attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.tools.IdurbaFormatV2;
import fr.ign.validator.cnig.validation.CnigValidatorTestBase;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.type.StringType;

public class IdurbaValidatorTest extends CnigValidatorTestBase {

    @Test
    public void testFormatV2() {
        IdurbaValidator validator = new IdurbaValidator(
            new IdurbaFormatV2(),
            "25349_CC_20010101"
        );
        // format v1
        assertFalse(validator.isValid("2534920010101"));
        assertFalse(validator.isValid("25349_20010101"));

        // format v2
        assertTrue(validator.isValid("25349_CC_20010101"));

    }

    @Test
    public void testValidDoesntReport() {
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "123456789_PLUI_20010101");

        IdurbaValidator validator = new IdurbaValidator(
            new IdurbaFormatV2(),
            "123456789_PLUi_20010101"
        );
        validator.validate(context, attribute);

        assertEquals(0, report.countErrors());
    }

    @Test
    public void testNotValidReport() {
        StringType type = new StringType();
        Attribute<String> attribute = new Attribute<String>(type, "25349_20010101");
        IdurbaValidator validator = new IdurbaValidator(
            new IdurbaFormatV2(),
            "25349_CC_20010101"
        );
        validator.validate(context, attribute);

        List<ValidatorError> errors = report.getErrorsByCode(CnigErrorCodes.CNIG_IDURBA_UNEXPECTED);
        assertEquals(1, errors.size());
        assertEquals(
            "La valeur du champ \"IDURBA\" (25349_20010101) ne correspond pas à la valeur attendue (25349_CC_20010101).",
            errors.get(0).getMessage()
        );
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
