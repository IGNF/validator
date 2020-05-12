package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SupCategoryTest {

    @Test
    public void testEmpty() {
        assertFalse(SupCategory.isValid(null));
        assertFalse(SupCategory.isValid(""));
    }

    @Test
    public void testValid() {
        assertTrue(SupCategory.isValid("AC1"));
        assertTrue(SupCategory.isValid("PM2"));
    }

    @Test
    public void testNotValid() {
        assertFalse(SupCategory.isValid("PM_2"));
        assertFalse(SupCategory.isValid("PM-1"));
    }

}
