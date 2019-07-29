package fr.ign.validator.cnig.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.ign.validator.cnig.model.MunicipalityCode;


public class MunicipalityCodeTest {
	
	@Test
	public void testValidCommune(){
		assertTrue(MunicipalityCode.isValidCommune("25349"));
		assertTrue(MunicipalityCode.isValidCommune("2B111"));
	}

	@Test	
	public void testNotValidCommune(){
		assertFalse(MunicipalityCode.isValidCommune("2534"));
		assertFalse(MunicipalityCode.isValidCommune("253495"));
		assertFalse(MunicipalityCode.isValidCommune("2C111"));
	}

}
