package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SirenCodeTest {

    @Test
    public void testEmpty() {
        assertFalse(SirenCode.isValid(null));
        assertFalse(SirenCode.isValid(""));
    }

    @Test
    public void testValid() {
        assertTrue(SirenCode.isValid("123123123"));
    }

    @Test
    public void testNotValid() {
        assertFalse(SirenCode.isValid("a23123123"));
        assertFalse(SirenCode.isValid("12312312"));
        assertFalse(SirenCode.isValid("1231231234"));
    }

}
