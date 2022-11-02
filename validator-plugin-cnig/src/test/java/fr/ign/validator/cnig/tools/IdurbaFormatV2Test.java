package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IdurbaFormatV2Test {

    private IdurbaFormatV2 format;

    @Before
    public void setUp() throws Exception {
        this.format = new IdurbaFormatV2();
    }

    @Test
    public void testEmptyOrNull() {
        assertFalse(format.isValid(""));
        assertFalse(format.isValid(null));
    }

    @Test
    public void testValid() {
        assertTrue(format.isValid("25349_PLU_20180101"));
        assertTrue(format.isValid("25349_PLU_20180101_A"));

        assertTrue(format.isValid("25349_POS_20180101"));
        assertTrue(format.isValid("25349_POS_20180101_A"));

        assertTrue(format.isValid("25349_CC_20180101"));
        assertTrue(format.isValid("25349_CC_20180101_A"));

        assertTrue(format.isValid("25349_PSMV_20180101"));
        assertTrue(format.isValid("25349_PSMV_20180101_A"));

        assertTrue(format.isValid("2A004_PLU_20180101"));
        assertTrue(format.isValid("2A004_PLU_20180101_A"));

        // lenient validation
        assertTrue(format.isValid("2A004_PLUi_20180101"));
        assertTrue(format.isValid("2A004_PLUi_20180101_A"));

        // sd-redmine-13106 - support the official form
        assertTrue(format.isValid("2A004_PLUI_20180101"));
        assertTrue(format.isValid("2A004_PLUI_20180101_A"));
    }

    @Test
    public void testNotValidOldStyle() {
        assertFalse(format.isValid("25349_20140101"));
        assertFalse(format.isValid("2B111_20140101"));
        assertFalse(format.isValid("2534920140101"));
    }

    @Test
    public void testGetHelpFormat() {
        assertEquals("<INSEE/SIREN>_<TYPEDOC>_<DATAPPRO>{_CodeDU}", format.getRegexpHelp());
    }

    @Test
    public void testGetHelpExpected() {
        assertEquals("25349_PLU_20180101", format.getRegexpHelp("25349_PLU_20180101"));
    }

}
