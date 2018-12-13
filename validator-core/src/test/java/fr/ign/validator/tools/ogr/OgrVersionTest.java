package fr.ign.validator.tools.ogr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OgrVersionTest {

	@Test
	public void testEmptyOrNull() {
		checkThrowOgrNotFound("");
		checkThrowOgrNotFound(null);
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
		checkThrowOgrNotFound("NOT VALID");
		checkThrowOgrNotFound("GDAL 1.15");
	}

	@Test
	public void testBannedVersion() {
		checkThrowOgrBadVersionException("GDAL 1.11.0");
	}

	private void checkThrowOgrNotFound(String fullVersion) {
		boolean thrown = false;
		try {
			new OgrVersion(fullVersion);
		} catch (OgrNotFoundException e) {
			thrown = true;
		}
		assertTrue("OgrNotFoundException expected", thrown);
	}

	private void checkThrowOgrBadVersionException(String fullVersion) {
		boolean thrown = false;
		try {
			new OgrVersion(fullVersion);
		} catch (OgrBadVersionException e) {
			thrown = true;
		}
		assertTrue("OgrBadVersionException expected", thrown);
	}

}
