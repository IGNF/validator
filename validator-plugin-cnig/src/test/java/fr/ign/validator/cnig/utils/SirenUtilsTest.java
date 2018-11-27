package fr.ign.validator.cnig.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class SirenUtilsTest {

	@Test
	public void testValid(){
		assertTrue(SirenUtils.isValid("123123123"));
	}

	@Test	
	public void testNotValid(){
		assertFalse(SirenUtils.isValid("a23123123"));
		assertFalse(SirenUtils.isValid("12312312"));
		assertFalse(SirenUtils.isValid("1231231234"));
	}
	
}
