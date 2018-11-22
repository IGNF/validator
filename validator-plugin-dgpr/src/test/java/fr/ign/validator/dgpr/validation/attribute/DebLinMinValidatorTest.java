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
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;

public class DebLinMinValidatorTest {

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

		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");
		
		DoubleType doubleTypeDebLin = new DoubleType();
		doubleTypeDebLin.setName("DEBLIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, 15.0);
		Attribute<Double> attribute2 = new Attribute<>(doubleTypeDebLin, 15.0);
		minValidator.validate(context, attribute);
		minValidator.validate(context, attribute2);

		Assert.assertEquals(0, report.countErrors());
	}

	
	@Test
	public void testError() throws Exception {
		// TODO partie contexte à revoir
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);

		DoubleType doubleTypeDebLinMin = new DoubleType();
		doubleTypeDebLinMin.setName("DEBLIN_MIN");
		
		DoubleType doubleTypeDebLin = new DoubleType();
		doubleTypeDebLin.setName("DEBLIN");

		// test
		DebLinMinValidator minValidator = new DebLinMinValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, -10.0);
		Attribute<Double> attribute2 = new Attribute<>(doubleTypeDebLin, -10.0);
		minValidator.validate(context, attribute);
		minValidator.validate(context, attribute2);

		Assert.assertEquals(2, report.countErrors());
		Assert.assertEquals("La valeur du débit -10.0 doit être supérieure à 0.", report.getErrorsByCode(DgprErrorCodes.DGPR_DEBLIN_MIN_ERROR).get(0).getMessage());
	}

}
