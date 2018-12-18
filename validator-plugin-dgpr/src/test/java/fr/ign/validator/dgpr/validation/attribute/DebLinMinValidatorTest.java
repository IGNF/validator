package fr.ign.validator.dgpr.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class DebLinMinValidatorTest {

	public static final Logger log = LogManager.getRootLogger();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	protected Context context;

	protected InMemoryReportBuilder report;


	@Before
	public void setUp() {
		report = new InMemoryReportBuilder();
		context = new Context();
		context.setReportBuilder(report);
	}


	@Test
	public void testValidate() throws Exception {
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, 15.0);
		minValidator.validate(context, attribute);

		Assert.assertEquals(0, report.countErrors());
	}


	@Test
	public void testError() throws Exception {
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, -10.0);
		minValidator.validate(context, attribute);

		Assert.assertEquals(1, report.countErrors());
		Assert.assertEquals("La valeur DEBLIN_MIN (-10.0) doit être supérieure à 0.", report.getErrorsByCode(DgprErrorCodes.DGPR_DEBLIN_MIN_ERROR).get(0).getMessage());
	}


	@Test
	public void testBindString() throws Exception {
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, "1.5");
		minValidator.validate(context, attribute);

		Assert.assertEquals(0, report.countErrors());
	}


	@Test
	public void testBindIllegalString() throws Exception {
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, "1,5");
		minValidator.validate(context, attribute);

		Assert.assertEquals(0, report.countErrors());
	}

}
