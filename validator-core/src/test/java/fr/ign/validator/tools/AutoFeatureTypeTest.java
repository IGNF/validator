package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;

public class AutoFeatureTypeTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * The column names come from the user data and are used in SQL queries.
     */
    @Test
    public void testInvalidColumnNamesIgnored() throws IOException {
        File path = new File(folder.getRoot(), "SAMPLE.csv");
        FileUtils.writeStringToFile(
            path,
            "ID,\"x TEXT); DROP TABLE t; --\",LIBELLE\n1,a,b\n",
            StandardCharsets.UTF_8
        );
        FeatureType featureType = AutoFeatureType.createFeatureTypeFromTable(path);
        assertEquals(2, featureType.getAttributeCount());
        assertNotNull(featureType.getAttribute("ID"));
        assertNotNull(featureType.getAttribute("LIBELLE"));
        assertNull(featureType.getAttribute("x TEXT); DROP TABLE t; --"));
    }

    @Test
    public void testCreateFromTAB() throws IOException {
        File path = ResourceHelper.getResourceFile(getClass(), "/data/tab_utf8/PRESCRIPTION_PCT.tab");
        FeatureType featureType = AutoFeatureType.createFeatureTypeFromTable(path);
        assertEquals("PRESCRIPTION_PCT", featureType.getName());

        /* ensure that WKT attribute is recognized as a WKT */
        {
            AttributeType<?> attribute = featureType.getAttribute("WKT");
            assertNotNull(attribute);
            assertTrue(attribute instanceof GeometryType);
        }

        /* ensure that other attributes are recognized as string */
        String[] otherNames = new String[] {
            "LIBELLE",
            "TXT",
            "TYPEPSC",
            "NOMFIC",
            "URLFIC",
            "INSEE",
            "DATAPPRO",
            "DATVALID"
        };
        for (String otherName : otherNames) {
            AttributeType<?> attribute = featureType.getAttribute(otherName);
            assertNotNull(attribute);
            assertTrue(attribute instanceof StringType);
        }

        /* check attribute count */
        assertEquals(otherNames.length + 1, featureType.getAttributeCount());
    }

}
