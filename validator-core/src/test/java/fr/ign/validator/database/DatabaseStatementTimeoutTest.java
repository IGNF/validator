package fr.ign.validator.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;

import org.junit.Test;

public class DatabaseStatementTimeoutTest {

    @Test
    public void testDefault() {
        assertEquals(Database.DEFAULT_STATEMENT_TIMEOUT, Database.getStatementTimeout(null));
        assertEquals(Database.DEFAULT_STATEMENT_TIMEOUT, Database.getStatementTimeout(""));
    }

    @Test
    public void testValid() {
        assertEquals(0, Database.getStatementTimeout("0"));
        assertEquals(1800, Database.getStatementTimeout("1800"));
        assertEquals(60, Database.getStatementTimeout(" 60 "));
    }

    @Test
    public void testInvalid() {
        for (String value : Arrays.asList("-1", "1h", "1.5", "60; DROP SCHEMA public")) {
            assertThrows(value, IllegalArgumentException.class, () -> Database.getStatementTimeout(value));
        }
    }

}
