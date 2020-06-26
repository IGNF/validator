package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IdurbaFormatV1Test {

    private IdurbaFormatV1 format;

    @Before
    public void setUp() throws Exception {
        this.format = new IdurbaFormatV1();
    }

    @Test
    public void testValidInseeWithSeparator() {
        assertTrue(format.isValid("25349_20140101"));
        assertTrue(format.isValid("2B111_20140101"));
    }

    @Test
    public void testValidInseeWithoutSeparator() {
        assertTrue(format.isValid("2534920140101"));
    }

    @Test
    public void testNotValid() {
        assertFalse(format.isValid(null));
        assertFalse(format.isValid("a254"));
        assertFalse(format.isValid("25349X_20140101")); // bad insee
        assertFalse(format.isValid("25349_201401015")); // bad date
    }

    @Test
    public void testNotValidStyle2017() {
        assertFalse(format.isValid("25349_CC_20180101"));
    }

    @Test
    public void testGetHelpFormat() {
        assertEquals("<INSEE/SIREN><DATAPPRO>", format.getRegexpHelp());
    }

    @Test
    public void testGetHelpExpected() {
        assertEquals("25349.*20180101.*", format.getRegexpHelp("25349_PLU_20180101"));
    }

    @Test
    public void testGetHelpExpectedPlui() {
        assertEquals("200011781.*20180101.*", format.getRegexpHelp("200011781_PLUI_20180101"));
        assertEquals("200011781.*20180101.*", format.getRegexpHelp("200011781_PLUi_20180101"));
    }

    @Test
    public void testGetHelpSuffix() {
        assertEquals("2A004.*20180101.*", format.getRegexpHelp("2A004_PLU_20180101_A"));
        assertEquals("200011781.*20180101.*", format.getRegexpHelp("200011781_PLUi_20180101_A"));
    }
}
