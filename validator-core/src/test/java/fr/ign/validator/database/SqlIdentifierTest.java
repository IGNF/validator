package fr.ign.validator.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SqlIdentifierTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testIsValid() {
        for (String name : Arrays.asList("ZONE_URBA", "gml_id", "_id", "N_prefixTri_ISO_HT_suffixIsoHt_S_ddd", "a1")) {
            assertTrue(name, SqlIdentifier.isValid(name));
        }
        for (String name : Arrays.asList(
            null, "", "1A", "LIB IDZONE", "x TEXT); DROP TABLE t; --", "../../tmp/x", "a-b", "a.b", "é"
        )) {
            assertFalse(String.valueOf(name), SqlIdentifier.isValid(name));
        }
    }

    @Test
    public void testCheck() {
        assertEquals("ZONE_URBA", SqlIdentifier.check("ZONE_URBA"));
        assertThrows(IllegalArgumentException.class, () -> SqlIdentifier.check("a; DROP TABLE b"));
    }

    /**
     * column names of the FeatureTypes created from the data are concatenated in
     * the CREATE TABLE query
     */
    @Test
    public void testCreateTableRejectsInjection() throws Exception {
        try (Database database = new Database(new File(folder.getRoot(), "sample.db"))) {
            assertThrows(
                IllegalArgumentException.class,
                () -> database.createTable("t", Arrays.asList("a TEXT); DROP TABLE x; --"))
            );
            assertThrows(
                IllegalArgumentException.class,
                () -> database.createTable("t; DROP TABLE x", Arrays.asList("a"))
            );
            database.createTable("t", Arrays.asList("a", "b"));
            assertEquals(0, database.getCount("t"));
        }
    }

}
