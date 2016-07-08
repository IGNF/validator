package fr.ign.validator.utils;

import junit.framework.TestCase;

public class IdurbaUtilsTest extends TestCase {

	public void testValidInseeWithSeparator(){
		assertTrue(IdurbaUtils.isValid("25349_20140101"));
		assertTrue(IdurbaUtils.isValid("2B111_20140101"));
	}

	public void testValidInseeWithoutSeparator(){
		assertTrue(IdurbaUtils.isValid("2534920140101"));
	}
	
	public void testNotValid(){
		assertFalse(IdurbaUtils.isValid(null)) ;
		assertFalse(IdurbaUtils.isValid("a254"));
		assertFalse(IdurbaUtils.isValid("25349X_20140101")); // bad insee
		assertFalse(IdurbaUtils.isValid("25349_201401015")); // bad date
	}
}

