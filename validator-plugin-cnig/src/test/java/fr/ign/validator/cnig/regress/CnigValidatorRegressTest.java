package fr.ign.validator.cnig.regress;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.CnigRegressHelper;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
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
		pluginManager.getPluginByName("CNIG").setup(context);
		return context;
	}

	/**
	 * 
	 * @param documentPath
	 * @return
	 */
	private File getGeneratedDocumentInfos(File documentPath){
		File validationDirectory = new File(documentPath.getParentFile(),"validation");
		return new File(validationDirectory,"document-info.json");
	}
	
	/**
	 * Test PLU en standard 2014
	 * 
	 * @throws Exception
	 */
	@Test
	public void test41175_PLU_20140603() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2013");

		File documentPath = CnigRegressHelper.getSampleDocument("41175_PLU_20140603",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("41175_PLU_20140603", document.getDocumentName());

			/* check ERRORS */
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.METADATA_CHARACTERSET_INVALID));
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR));
			Assert.assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND));
			// relative to DOC_URBA.DATEREF
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP));
			
			Assert.assertEquals(4, report.countErrors(ErrorLevel.ERROR));
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

		File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20130902",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("50545_CC_20130902", document.getDocumentName());
			/* check errors */
			Assert.assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND));
			Assert.assertEquals(1, report.countErrors(CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND));
			Assert.assertEquals(2, report.countErrors(ErrorLevel.ERROR));
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

		File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20140101",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("50545_CC_20140101", document.getDocumentName());
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.NO_SPATIAL_DATA));
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.ATTRIBUTE_UNEXPECTED_NULL));
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.FILE_MISSING_MANDATORY));
			Assert.assertEquals(3, report.countErrors(ErrorLevel.ERROR));
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
		/* allows to skip some tests if GDAL breaks coordinates precision */
		boolean gdalDestroysCoordinates = FileConverter.getInstance().getVersion().startsWith("GDAL 1.");

		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2017");

		File documentPath = CnigRegressHelper.getSampleDocument("19182_CC_20150517",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("19182_CC_20150517", document.getDocumentName());
			/* check errors */
			// DOC_URBA.DATEREF = 2010 (bad regexp)
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP));
			Assert.assertEquals(1, report.countErrors(ErrorLevel.ERROR));
			
			/* check warnings */
			Assert.assertEquals(3, report.countErrors(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND));
			if ( gdalDestroysCoordinates ){
				// GDAL 1.10.1 and 1.11.3 changes coordinates so that it turns invalid geometries to valid geometries...
				Assert.assertEquals(0, report.countErrors(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID));				
				Assert.assertEquals(3, report.countErrors(ErrorLevel.WARNING));
			}else{
				Assert.assertEquals(2, report.countErrors(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID));				
				Assert.assertEquals(5, report.countErrors(ErrorLevel.WARNING));
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
		if ( ! gdalDestroysCoordinates ){
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
		File documentPath = CnigRegressHelper.getSampleDocument("110068012_PM3_28_20161104",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("110068012_PM3_28_20161104", document.getDocumentName());
			Assert.assertEquals(0, report.countErrors(ErrorLevel.ERROR));
			Assert.assertEquals(0, report.countErrors(ErrorLevel.WARNING));
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
	 * SUP with duplicated values in AC1_ACTE_SUP.dbf (ex : "AC1-172014607-00099077-1", "AC1-172014607-00099077-1")
	 * 
	 * (was previously crashing SQLITE database insertion)
	 * 
	 * @throws Exception
	 */
	@Test
	public void test172014607_AC1_2A_20180130() throws Exception {
		DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_AC1_2016");
		File documentPath = CnigRegressHelper.getSampleDocument("172014607_AC1_2A_20180130",folder);
		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("172014607_AC1_2A_20180130", document.getDocumentName());
			Assert.assertEquals(0, report.countErrors(ErrorLevel.ERROR));
			Assert.assertEquals(1, report.countErrors(ErrorLevel.WARNING));
			Assert.assertEquals(1, report.countErrors(CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND));
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

}
