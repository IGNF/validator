package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MunicipalityCodeTest {

    @Test
    public void testEmpty() {
        assertFalse(MunicipalityCode.isValid(null));
        assertFalse(MunicipalityCode.isValid(""));
    }

    @Test
    public void testValidCommune() {
        assertTrue(MunicipalityCode.isValid("25349"));
        assertTrue(MunicipalityCode.isValid("2B111"));
    }

    @Test
    public void testNotValidCommune() {
        assertFalse(MunicipalityCode.isValid("2534"));
        assertFalse(MunicipalityCode.isValid("253495"));
        assertFalse(MunicipalityCode.isValid("2C111"));
        assertFalse(MunicipalityCode.isValid("25-95"));
    }

}
