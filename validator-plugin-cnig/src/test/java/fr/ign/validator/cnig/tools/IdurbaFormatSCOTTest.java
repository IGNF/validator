package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class IdurbaFormatSCOTTest {

    private IdurbaFormatSCOT format;

    @Before
    public void setUp() throws Exception {
        this.format = new IdurbaFormatSCOT();
    }

    @Test
    public void testEmptyOrNull() {
        assertFalse(format.isValid(""));
        assertFalse(format.isValid(null));
    }

    @Test
    public void testValid() {
        assertTrue(format.isValid("123456789_SCOT_20200101"));
        assertTrue(format.isValid("123456789_SCOT_20200101_A"));
        assertTrue(format.isValid("123456789_SCOT_20200101_C"));
    }

    @Test
    public void testNotValid() {
        assertFalse(format.isValid("25349_PLU_20180101"));
        assertFalse(format.isValid("25349_PLU_20180101_A"));

        assertFalse(format.isValid("25349_SCOT_20180101"));
        assertFalse(format.isValid("25349_SCOT_20180101_B"));
    }

    @Test
    public void testGetHelpFormat() {
        assertEquals("<SIREN>_SCOT_<DATAPPRO>{_CodeDU}", format.getRegexpHelp());
    }

    @Test
    public void testGetHelpExpected() {
        assertEquals("123456789_SCOT_20200101", format.getRegexpHelp("123456789_SCOT_20200101"));
    }

}
