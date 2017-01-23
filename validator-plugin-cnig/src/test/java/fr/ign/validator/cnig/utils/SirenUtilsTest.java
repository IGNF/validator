package fr.ign.validator.cnig.utils;

import junit.framework.TestCase;

public class SirenUtilsTest extends TestCase {

	public void testValid(){
		assertTrue(SirenUtils.isValid("123123123"));
	}
	
	public void testNotValid(){
		assertFalse(SirenUtils.isValid("a23123123"));
		assertFalse(SirenUtils.isValid("12312312"));
		assertFalse(SirenUtils.isValid("1231231234"));
	}
	
}
