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
import fr.ign.validator.model.type.IntegerType;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;

public class TauxHabitantValidatorTest {

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
		
		// le modele
		IntegerType integerTypeTxhab = new IntegerType();
		integerTypeTxhab.setName("TX_HAB_SAI");
		
		// le test
		TauxHabitantValidator txHabValidator = new TauxHabitantValidator();
		Attribute<Integer> attribute = new Attribute<>(integerTypeTxhab, 50);
		txHabValidator.validate(context, attribute);

		Assert.assertEquals(0, report.countErrors());
	}
	
	@Test
	public void testError() throws Exception {
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);
		
		// le modele
		IntegerType integerTypeTxhab = new IntegerType();
		integerTypeTxhab.setName("TX_HAB_SAI");
		
		// le test
		TauxHabitantValidator txHabValidator = new TauxHabitantValidator();
		Attribute<Integer> attribute = new Attribute<>(integerTypeTxhab, 200);
		txHabValidator.validate(context, attribute);

		Assert.assertEquals(1, report.countErrors());	
		Assert.assertEquals("La valeur de TX_HAB_SAI 200 n'est pas comprise entre 0 et 100.", report.getErrorsByCode(DgprErrorCodes.DGPR_TX_HAB_SAI_ERROR).get(0).getMessage());	
	}
	
	@Test
	public void testNullError() throws Exception {
		File documentPath = getSampleDocument("sample_document");
		Context context = createContext(documentPath);
		
		// le modele
		IntegerType integerTypeTxhab = new IntegerType();
		integerTypeTxhab.setName("TX_HAB_SAI");
		
		// le test
		TauxHabitantValidator txHabValidator = new TauxHabitantValidator();
		Attribute<Integer> attribute = new Attribute<>(integerTypeTxhab, null);
		txHabValidator.validate(context, attribute);

		Assert.assertEquals(0, report.countErrors());	
	}
}