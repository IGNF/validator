package fr.ign.validator.dgpr.validation.attribute;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.referencing.CRS;
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
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;

public class DebLinMaxValidatorTest {

	public static final Logger log = LogManager.getRootLogger();

	protected InMemoryReportBuilder report;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Before
	public void setUp() {
		report = new InMemoryReportBuilder();
	}

	private Context createContext(File documentPath) throws Exception {
		Context context = new Context();
		context.setReportBuilder(report);
		context.setCoordinateReferenceSystem(CRS.decode("EPSG:2154"));
		File validationDirectory = new File(documentPath.getParentFile(), "validation");
		context.setValidationDirectory(validationDirectory);
		PluginManager pluginManager = new PluginManager();
		pluginManager.getPluginByName("DGPR").setup(context);
		return context;
	}

	private File getSampleDocument(String documentName) throws IOException {
		URL resource = getClass().getResource("/documents/" + documentName);
		Assert.assertNotNull(resource);
		File sourcePath = new File(resource.getPath());

		File documentPath = folder.newFolder(documentName);
		FileUtils.copyDirectory(sourcePath, documentPath);
		return documentPath;
	}

	
	@Test
	public void testValidate() throws Exception {
		// TODO partie contexte à revoir
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);

		// le csv
		String[] header = {"DEBLIN_MIN"};
		String[] values = {"1.0"};

		// le modele
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		DoubleType doubleTypeDebLinMax = new DoubleType();
		doubleTypeDebLinMax.setName("DEBLIN_MAX");

		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeDebLinMin);
		featureType.addAttribute(doubleTypeDebLinMax);

		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);

		// test
		DebLinMaxValidator minValidator = new DebLinMaxValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 1.5);
		minValidator.validate(context, attribute);

		context.beginData(row);

		Assert.assertEquals(0, report.countErrors());
	}

	
	@Test
	public void testValueMaxError() throws Exception {
		// TODO partie contexte à revoir
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);

		// le csv
		String[] header = {"DEBLIN_MIN"};
		String[] values = {"1.0"};

		// le modele
		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");

		DoubleType doubleTypeDebLinMax = new DoubleType();
		doubleTypeDebLinMax.setName("DEBLIN_MAX");

		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeDebLinMin);
		featureType.addAttribute(doubleTypeDebLinMax);

		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);

		// test
		DebLinMaxValidator minValidator = new DebLinMaxValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 0.9);
		minValidator.validate(context, attribute);

		context.beginData(row);

		Assert.assertEquals(1, report.countErrors());
		Assert.assertEquals("La valeur DEBLIN_MAX 0.9 doit être nulle ou supérieure la valeur DEBLIN_MIN 1.0", report.getErrorsByCode(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR).get(0).getMessage());
	}

}
