package fr.ign.validator.cnig.idurba.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IdurbaHelperV1Test {

    private IdurbaHelperV1 idurbaHelper;

    @Before
    public void setUp() throws Exception {
        this.idurbaHelper = new IdurbaHelperV1();
    }

    @Test
    public void testValidInseeWithSeparator() {
        assertTrue(idurbaHelper.isValid("25349_20140101"));
        assertTrue(idurbaHelper.isValid("2B111_20140101"));
    }

    @Test
    public void testValidInseeWithoutSeparator() {
        assertTrue(idurbaHelper.isValid("2534920140101"));
    }

    @Test
    public void testNotValid() {
        assertFalse(idurbaHelper.isValid(null));
        assertFalse(idurbaHelper.isValid("a254"));
        assertFalse(idurbaHelper.isValid("25349X_20140101")); // bad insee
        assertFalse(idurbaHelper.isValid("25349_201401015")); // bad date
    }

    @Test
    public void testNotValidStyle2017() {
        assertFalse(idurbaHelper.isValid("25349_CC_20180101"));
    }

    @Test
    public void testGetHelpFormat() {
        assertEquals("<INSEE/SIREN><DATAPPRO>", idurbaHelper.getHelpFormat());
    }

    @Test
    public void testGetHelpExpected() {
        assertEquals("25349.*20180101.*", idurbaHelper.getHelpExpected("25349_PLU_20180101"));
    }

    @Test
    public void testGetHelpExpectedPlui() {
        assertEquals("200011781.*20180101.*", idurbaHelper.getHelpExpected("200011781_PLUI_20180101"));
        assertEquals("200011781.*20180101.*", idurbaHelper.getHelpExpected("200011781_PLUi_20180101"));
    }

    @Test
    public void testGetHelpSuffix() {
        assertEquals("2A004.*20180101.*", idurbaHelper.getHelpExpected("2A004_PLU_20180101_A"));
        assertEquals("200011781.*20180101.*", idurbaHelper.getHelpExpected("200011781_PLUi_20180101_A"));
    }
}
