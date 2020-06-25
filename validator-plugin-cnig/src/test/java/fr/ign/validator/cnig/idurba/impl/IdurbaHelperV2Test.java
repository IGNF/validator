package fr.ign.validator.cnig.idurba.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IdurbaHelperV2Test {

    private IdurbaHelperV2 idurbaHelper;

    @Before
    public void setUp() throws Exception {
        this.idurbaHelper = new IdurbaHelperV2();
    }

    @Test
    public void testValid() {
        assertTrue(idurbaHelper.isValid("25349_PLU_20180101"));
        assertTrue(idurbaHelper.isValid("25349_PLU_20180101_A"));

        assertTrue(idurbaHelper.isValid("25349_POS_20180101"));
        assertTrue(idurbaHelper.isValid("25349_POS_20180101_A"));

        assertTrue(idurbaHelper.isValid("25349_CC_20180101"));
        assertTrue(idurbaHelper.isValid("25349_CC_20180101_A"));

        assertTrue(idurbaHelper.isValid("25349_PSMV_20180101"));
        assertTrue(idurbaHelper.isValid("25349_PSMV_20180101_A"));

        assertTrue(idurbaHelper.isValid("2A004_PLU_20180101"));
        assertTrue(idurbaHelper.isValid("2A004_PLU_20180101_A"));
    }

    @Test
    public void testNotValidOldStyle() {
        assertFalse(idurbaHelper.isValid("25349_20140101"));
        assertFalse(idurbaHelper.isValid("2B111_20140101"));
        assertFalse(idurbaHelper.isValid("2534920140101"));
    }

    @Test
    public void testGetHelpFormat() {
        assertEquals("<INSEE/SIREN>_<TYPEDOC>_<DATAPPRO>{_CodeDU}", idurbaHelper.getHelpFormat());
    }

    @Test
    public void testGetHelpExpected() {
        assertEquals("25349_PLU_20180101", idurbaHelper.getHelpExpected("25349_PLU_20180101"));
    }

}
