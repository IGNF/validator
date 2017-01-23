package fr.ign.validator.cnig.utils;

import junit.framework.TestCase;

public class InseeUtilsTest extends TestCase {
	
	public void testValidCommune(){
		assertTrue(InseeUtils.isValidCommune("25349"));
		assertTrue(InseeUtils.isValidCommune("2B111"));
	}
	
	public void testNotValidCommune(){
		assertFalse(InseeUtils.isValidCommune("2534"));
		assertFalse(InseeUtils.isValidCommune("253495"));
		assertFalse(InseeUtils.isValidCommune("2C111"));
	}
	
	public void testValidDepartement(){
		assertTrue(InseeUtils.isValidDepartement("2B"));
		assertTrue(InseeUtils.isValidDepartement("2A"));
		assertTrue(InseeUtils.isValidDepartement("01"));
		assertTrue(InseeUtils.isValidDepartement("971"));
	}
}
