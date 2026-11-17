package fr.ign.validator.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;

import org.junit.Test;

/**
 * DB_SCHEMA is concatenated in DROP SCHEMA ... CASCADE, CREATE SCHEMA and SET
 * search_path.
 */
public class DatabaseSchemaNameTest {

    @Test
    public void testDefault() {
        assertEquals("validation", Database.getSchemaName(null));
        assertEquals("validation", Database.getSchemaName(""));
    }

    @Test
    public void testValid() {
        assertEquals("validation6ad0e5b3c1", Database.getSchemaName("validation6ad0e5b3c1"));
        assertEquals("validation_test", Database.getSchemaName("validation_test"));
    }

    @Test
    public void testForbidden() {
        for (String value : Arrays.asList(
            "public", "PUBLIC", "Public", "pg_catalog", "PG_TOAST", "information_schema",
            "validation; DROP SCHEMA public CASCADE", "\"public\"", "a b", "1abc"
        )) {
            assertThrows(value, RuntimeException.class, () -> Database.getSchemaName(value));
        }
    }

}
