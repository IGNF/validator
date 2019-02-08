package fr.ign.validator.cnig.regress;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.CnigRegressHelper;
import fr.ign.validator.cnig.ReportAssert;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.FileConverter;

/**
 * 
 * Test de régression sur la validation avec le plugin CNIG activé avec contrôle
 * de :
 * 
 * <ul>
 * <li>La stabilité de cnig-info.xml
 * <li>
 * <li>La stabilité des erreurs (TODO : à améliorer)</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class CnigValidatorRegressTest {

	public static final Logger log = LogManager.getRootLogger();

	protected InMemoryReportBuilder report;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	/*
	 * allows to skip some tests if GDAL breaks coordinates precision
	 */
	private boolean gdalDestroysCoordinates;

	@Before
	public void setUp() {
		report = new InMemoryReportBuilder();

		gdalDestroysCoordinates = FileConverter.getInstance().isBreakingCoordinatePrecision();
	}

	/**
	 * Create validation context
	 * 
	 * @param documentPath
	 * @return
	 * @throws Exception
	 */
	private Context createContext(File documentPath) throws Exception {
		Context context = new Context();
		context.setReportBuilder(report);
		context.setProjection("EPSG:2154");
		File validationDirectory = new File(documentPath.getParentFile(), "validation");
		context.setValidationDirectory(validationDirectory);
		PluginManager pluginManager = new PluginManager();
		pluginManager.getPluginByName("CNIG").setup(context);
		return context;
	}

	/**
	 * Get generated document-info.json file
	 * 
	 * @param documentPath
	 * @return
	 */
	private File getGeneratedDocumentInfos(File documentPath) {
		File validationDirectory = new File(documentPath.getParentFile(), "validation");
		return new File(validationDirectory, "document-info.json");
	}

	/**
	 * Test PLU en standard 2014
	 * 
	 * @throws Exception
	 */
	@Test
	public void test41175_PLU_20140603() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2013");

		File documentPath = CnigRegressHelper.getSampleDocument("41175_PLU_20140603", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("41175_PLU_20140603", document.getDocumentName());

			ReportAssert.assertCount(4, ErrorLevel.ERROR, report);

			/* check ERRORS */
			ReportAssert.assertCount(1, CoreErrorCodes.METADATA_CHARACTERSET_INVALID, report);
			ReportAssert.assertCount(1, CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR, report);
			ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_IDENTIFIER_INVALID, report);
			// relative to DOC_URBA.DATEREF
			ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("41175_PLU_20140603");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * Test CC en standard 2013
	 * 
	 * @throws Exception
	 */
	@Test
	public void test50545_CC_20130902() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2013");

		File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20130902", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("50545_CC_20130902", document.getDocumentName());
			/* check errors */
			ReportAssert.assertCount(2, ErrorLevel.ERROR, report);
			ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND, report);
			ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND, report);

			ReportAssert.assertCount(7, ErrorLevel.WARNING, report);
			ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, report);
			ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
			ReportAssert.assertCount(1, CoreErrorCodes.TABLE_MISSING_NULLABLE_ATTRIBUTE, report);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("50545_CC_20130902");
		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * Test CC en standard 2014 avec aucune donnée géographique
	 * (CNIG_NO_SPATIAL_DATA)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test50545_CC_20140101() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2014");

		File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20140101", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("50545_CC_20140101", document.getDocumentName());

			ReportAssert.assertCount(4, ErrorLevel.ERROR, report);
			ReportAssert.assertCount(1, CoreErrorCodes.NO_SPATIAL_DATA, report);
			ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_UNEXPECTED_NULL, report);
			ReportAssert.assertCount(1, CoreErrorCodes.FILE_MISSING_MANDATORY, report);
			ReportAssert.assertCount(1, CnigErrorCodes.CNIG_IDURBA_NOT_FOUND, report);

			ReportAssert.assertCount(6, ErrorLevel.WARNING, report);
			ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
			ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, report);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("50545_CC_20140101");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * Test en standard 2017 (IDURBA change de format)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test19182_CC_20150517() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2017");

		File documentPath = CnigRegressHelper.getSampleDocument("19182_CC_20150517", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("19182_CC_20150517", document.getDocumentName());
			if (gdalDestroysCoordinates) {
				/* check errors */
				ReportAssert.assertCount(1, ErrorLevel.ERROR, report);
				// DOC_URBA.DATEREF = 2010 (bad regexp)
				ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);

				ReportAssert.assertCount(3, ErrorLevel.WARNING, report);
				ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);

				// GDAL 1.10.1 and 1.11.3 changes coordinates so that it turns
				// invalid geometries to valid geometries...
				ReportAssert.assertCount(0, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
			} else {
				/* check errors */
				ReportAssert.assertCount(3, ErrorLevel.ERROR, report);
				// DOC_URBA.DATEREF = 2010 (bad regexp)
				ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);

				ReportAssert.assertCount(3, ErrorLevel.WARNING, report);

				ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
				ReportAssert.assertCount(2, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("19182_CC_20150517");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		/* skips tests for GDAL 1.11 */
		if (!gdalDestroysCoordinates) {
			JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
		}
	}

	/**
	 * Test SUP
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSUP_PM3_28() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_PM3_2013");
		File documentPath = CnigRegressHelper.getSampleDocument("110068012_PM3_28_20161104", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("110068012_PM3_28_20161104", document.getDocumentName());
			ReportAssert.assertCount(0, ErrorLevel.ERROR, report);
			ReportAssert.assertCount(0, ErrorLevel.WARNING, report);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("110068012_PM3_28_20161104");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * SUP with duplicated values in AC1_ACTE_SUP.dbf (ex :
	 * "AC1-172014607-00099077-1", "AC1-172014607-00099077-1")
	 * 
	 * (was previously crashing SQLITE database insertion)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test172014607_AC1_2A_20180130() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_AC1_2016");
		File documentPath = CnigRegressHelper.getSampleDocument("172014607_AC1_2A_20180130", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("172014607_AC1_2A_20180130", document.getDocumentName());
			ReportAssert.assertCount(0, ErrorLevel.ERROR, report);
			ReportAssert.assertCount(1, ErrorLevel.WARNING, report);
			ReportAssert.assertCount(1, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("172014607_AC1_2A_20180130");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * Test PLU avec coordonnées 3D en standard cnig_PLU_2017
	 * 
	 * @throws Exception
	 */
	@Test
	public void test30014_PLU_20171013() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2017");

		File documentPath = CnigRegressHelper.getSampleDocument("30014_PLU_20171013", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("30014_PLU_20171013", document.getDocumentName());
			ReportAssert.assertCount(0, ErrorLevel.ERROR, report);
			ReportAssert.assertCount(0, ErrorLevel.WARNING, report);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("30014_PLU_20171013");

		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

	/**
	 * Test case sensitivity for IDURBA and "PLUi" 200011781_PLUi_20180101
	 */
	@Test
	public void test200011781_PLUi_20180101() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLUi_2014");

		File documentPath = CnigRegressHelper.getSampleDocument("200011781_PLUi_20180101", folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("200011781_PLUi_20180101", document.getDocumentName());

			if (!gdalDestroysCoordinates) {
				ReportAssert.assertCount(4, ErrorLevel.ERROR, report);
				ReportAssert.assertCount(4, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
			} else {
				ReportAssert.assertCount(0, ErrorLevel.ERROR, report);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}

		File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
		String actual = FileUtils.readFileToString(producedInfosCnigPath).trim();

		File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("200011781_PLUi_20180101");
		String expected = FileUtils.readFileToString(expectedInfosCnigPath).trim();
		JSONAssert.assertEquals(expected, actual, JSONCompareMode.LENIENT);
	}

}
