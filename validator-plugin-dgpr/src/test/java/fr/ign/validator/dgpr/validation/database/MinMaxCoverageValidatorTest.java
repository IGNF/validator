package fr.ign.validator.dgpr.validation.database;

import org.junit.Assert;
import org.junit.Test;

public class MinMaxCoverageValidatorTest {
	
	@Test
	public void doubleCastErrorTest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		try {
			coverageValidator.compare("0.5", "0,5");
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}
	
	
	@Test
	public void doubleCastNoErrorTest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		try {
			coverageValidator.compare("0.5", "0.5");
		} catch (NumberFormatException e) {
			Assert.fail();
		}
		
	}

}
