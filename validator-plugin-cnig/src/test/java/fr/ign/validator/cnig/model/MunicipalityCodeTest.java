package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.ign.validator.cnig.model.MunicipalityCode;


public class MunicipalityCodeTest {
	
	@Test
	public void testValidCommune(){
		assertTrue(MunicipalityCode.isValid("25349"));
		assertTrue(MunicipalityCode.isValid("2B111"));
	}

	@Test	
	public void testNotValidCommune(){
		assertFalse(MunicipalityCode.isValid("2534"));
		assertFalse(MunicipalityCode.isValid("253495"));
		assertFalse(MunicipalityCode.isValid("2C111"));
	}

}
