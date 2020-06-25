package fr.ign.validator.tools.ogr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OgrVersionTest {

    @Test
    public void testEmptyOrNull() {
        assertThatParsingThrow("");
        assertThatParsingThrow(null);
    }

    @Test
    public void testGetMajorMinorPatch() {
        OgrVersion version = new OgrVersion("GDAL 1.11.5");
        assertEquals(1, version.getMajor());
        assertEquals(11, version.getMinor());
        assertEquals(5, version.getPatch());
    }

    @Test
    public void testBadFormat() {
        assertThatParsingThrow("NOT VALID");
        assertThatParsingThrow("GDAL 1.15");
    }

    @Test
    public void testEnsureVersionIsSupported() {
        assertThatEnsureVersionIsSupportedThrow("GDAL 1.11.0");
        assertThatEnsureVersionIsSupportedThrow("GDAL 2.2.0");
        // greater than 2.3.0 is ok
        assertThatEnsureVersionIsSupportedDoesntThrow("GDAL 2.3.0");
        assertThatEnsureVersionIsSupportedDoesntThrow("GDAL 2.4.2");
        assertThatEnsureVersionIsSupportedDoesntThrow("GDAL 3.0.0");
    }

    private void assertThatParsingThrow(String fullVersion) {
        boolean thrown = false;
        try {
            new OgrVersion(fullVersion);
        } catch (OgrNotFoundException e) {
            thrown = true;
        }
        assertTrue("OgrBadVersionException expected", thrown);
    }

    private void assertThatEnsureVersionIsSupportedThrow(String fullVersion) {
        OgrVersion version = new OgrVersion(fullVersion);
        boolean thrown = false;
        try {
            version.ensureVersionIsSupported();
        } catch (OgrBadVersionException e) {
            thrown = true;
        }
        assertTrue("OgrNotFoundException expected", thrown);
    }

    private void assertThatEnsureVersionIsSupportedDoesntThrow(String fullVersion) {
        OgrVersion version = new OgrVersion(fullVersion);
        boolean thrown = false;
        try {
            version.ensureVersionIsSupported();
        } catch (OgrBadVersionException e) {
            thrown = true;
        }
        assertFalse("OgrNotFoundException is not expected", thrown);
    }

}
