package fr.ign.validator.model.type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class IntegerTypeTest extends AbstractTypeTest<Integer> {

    public IntegerTypeTest() {
        super(new IntegerType());
    }

    @Test
    public void testBindRealInteger() {
        assertEquals(1, type.bind("1").intValue());
        assertEquals(5, type.bind("5").intValue());
        assertEquals(4751, type.bind("4751").intValue());
    }

    @Test
    public void testBindRoundableDouble() {
        assertEquals(1, type.bind("1.00000").intValue());
        assertEquals(50, type.bind("50.00000").intValue());
        assertEquals(4751, type.bind("4751.00000").intValue());
        assertEquals(4751, type.bind("4751.00000000000001").intValue());
    }

    @Test
    public void testBindNotRoundableDouble() {
        assertBindThrow("50.1");
        assertBindThrow("1.00001");
        assertBindThrow("4751.000001");
    }

    @Test
    public void testBindError() {
        assertBindThrow("t");
        assertBindThrow("false");
    }

    @Test
    public void testBindLimits() {
        assertEquals(Integer.MAX_VALUE, type.bind("2147483647").intValue());
        assertEquals(Integer.MIN_VALUE, type.bind("-2147483648").intValue());
        assertEquals(Integer.MAX_VALUE, type.bind("2147483647.0").intValue());
        assertEquals(Integer.MIN_VALUE, type.bind("-2147483648.0").intValue());
    }

    /**
     * Values outside the int range were silently clamped to 2147483647 (ex :
     * IDASS=130010408211 in a SUP 2013 document)
     */
    @Test
    public void testBindOverflowThrow() {
        assertBindThrow("2147483648");
        assertBindThrow("-2147483649");
        assertBindThrow("2147483648.0");
        assertBindThrow("130010408211");
        assertBindThrow("1300104081011");
        assertBindThrow("1e10");
        assertBindThrow("NaN");
        assertBindThrow("Infinity");
        assertBindThrow("-Infinity");
    }

    private void assertBindThrow(String input) {
        boolean thrown = false;
        try {
            type.bind(input).intValue();
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue("'" + input + "' should be rejected", thrown);
    }

}
