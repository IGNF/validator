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

public class AzimuthValidatorTest {

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
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);
		
		// le csv
		String[] header = {"VITESSE","DEBLIN"};
		String[] values = {"1.0","0.0"};
		
		//le modele
		DoubleType doubleTypeAzimuth = new DoubleType();
		doubleTypeAzimuth.setName("AZIMUTH");
		
		DoubleType doubleTypeVitesse = new DoubleType();
		doubleTypeVitesse.setName("VITESSE");
		
		DoubleType doubleTypeDeblin = new DoubleType();
		doubleTypeDeblin.setName("DEBLIN");
		
		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeAzimuth);
		featureType.addAttribute(doubleTypeVitesse);
		featureType.addAttribute(doubleTypeDeblin);
		
		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
		
		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);
		
		// le test
		AzimuthValidator azimuthValidator = new AzimuthValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, 15.0);
		azimuthValidator.validate(context, attribute);
		
		context.beginData(row);

		Assert.assertEquals(0, report.countErrors());	
	}
	
	@Test
	public void testError() throws Exception {
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);
		
		// le csv
		String[] header = {"VITESSE","DEBLIN"};
		String[] values = {"1.0","0.0"};
		
		//le modele
		DoubleType doubleTypeAzimuth = new DoubleType();
		doubleTypeAzimuth.setName("AZIMUTH");
		
		DoubleType doubleTypeVitesse = new DoubleType();
		doubleTypeVitesse.setName("VITESSE");
		
		DoubleType doubleTypeDeblin = new DoubleType();
		doubleTypeDeblin.setName("DEBLIN");
		
		FeatureType featureType = new FeatureType();
		featureType.addAttribute(doubleTypeAzimuth);
		featureType.addAttribute(doubleTypeVitesse);
		featureType.addAttribute(doubleTypeDeblin);
		
		FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
		
		// la ligne
		Row row = new Row(0, values, mapping);
		context.beginData(row);
		
		// le test
		AzimuthValidator azimuthValidator = new AzimuthValidator();
		Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
		azimuthValidator.validate(context, attribute);
		
		context.beginData(row);

		Assert.assertEquals(1, report.countErrors());	
		Assert.assertEquals("La vitesse (1.0) et/ou le débit linéique (0.0) est renseigné, mais l’azimuth est null.", report.getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage());
	}
	
}
