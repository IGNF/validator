package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;

public class AutoFeatureTypeTest {

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
