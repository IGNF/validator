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
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class VitesseMinValidatorTest {

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
		// le csv
		String[] header = {"VITESS_MAX"};
		String[] values = {"30.0"};

		// le modele
		DoubleType doubleTypeMax = new DoubleType();
		doubleTypeMax.setName("VITESS_MAX");

		DoubleType doubleTypeMin = new DoubleType();
		doubleTypeMin.setName("VITESS_MIN");

		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeMax);
		featureType.addAttribute(doubleTypeMin);

		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);

		// test
		VitesseMinValidator minValidator = new VitesseMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeMin, 15.0);
		minValidator.validate(context, attribute);

		context.beginData(row);

		Assert.assertEquals(0, report.countErrors());
	}


	@Test
	public void testNullValidate() throws Exception {
		// le csv
		String[] header = {"VITESS_MAX"};
		String[] values = {null};

		// le modele
		DoubleType doubleTypeMax = new DoubleType();
		doubleTypeMax.setName("VITESS_MAX");

		DoubleType doubleTypeMin = new DoubleType();
		doubleTypeMin.setName("VITESS_MIN");

		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeMax);
		featureType.addAttribute(doubleTypeMin);

		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);

		// test
		VitesseMinValidator minValidator = new VitesseMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeMin, 15.0);
		minValidator.validate(context, attribute);

		context.beginData(row);

		Assert.assertEquals(0, report.countErrors());
	}


	@Test
	public void testValueMaxError() throws Exception {
		// le csv
		String[] header = {"VITESS_MAX"};
		String[] values = {"30.0"};

		// le modele
		DoubleType doubleTypeMax = new DoubleType();
		doubleTypeMax.setName("VITESS_MAX");

		DoubleType doubleTypeMin = new DoubleType();
		doubleTypeMin.setName("VITESS_MIN");

		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeMax);
		featureType.addAttribute(doubleTypeMin);

		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);

		// test
		VitesseMinValidator minValidator = new VitesseMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeMin, 31.0);
		minValidator.validate(context, attribute);

		context.beginData(row);

		Assert.assertEquals(1, report.countErrors());
		Assert.assertEquals("La vitesse MIN 31.0 est supérieure à la vitesse MAX 30.0.", report.getErrorsByCode(DgprErrorCodes.DGPR_VITESSE_MIN_ERROR).get(0).getMessage());
	}

}
