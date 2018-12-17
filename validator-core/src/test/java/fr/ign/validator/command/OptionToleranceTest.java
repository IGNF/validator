package fr.ign.validator.command;

import org.junit.Assert;
import org.junit.Test;

public class OptionToleranceTest {

	@Test
	public void testParseDouble() {
		String string = "1.0";

		Double number = Double.valueOf(string);

		Assert.assertNotEquals(number, Double.NaN);
	}


	@Test
	public void testParseException() {
		String string = "ceci n'est pas un nombre";
		try {
			Double.valueOf(string);
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}


	@Test
	public void testParseNan() {
		String string = "NaN";
		try {
			Double.valueOf(string);
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}


	@Test
	public void testMixedValue() {
		String string = "10e-1";
		try {
			Double.valueOf(string);
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}


	@Test
	public void testDoubleFormat() {
		String string = "1.0d";
		try {
			Double.valueOf(string);
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}

}
