package fr.ign.validator;

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

import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.xml.XmlModelManager;

/**
 *
 */
public class DgprApplicationTest {

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

	private DocumentModel getDocumentModel(String documentModelName) throws Exception {
		File documentModelPath = new File(getClass().getResource("/config/" + documentModelName + "/files.xml").getPath());
		XmlModelManager loader = new XmlModelManager();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
		documentModel.setName(documentModelName);
		return documentModel;
	}

	private File getSampleDocument(String documentName) throws IOException {
		URL resource = getClass().getResource("/documents/" + documentName);
		Assert.assertNotNull(resource);
		File sourcePath = new File(resource.getPath());

		File documentPath = folder.newFolder(documentName);
		FileUtils.copyDirectory(sourcePath, documentPath);
		return documentPath;
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDocument() throws Exception {
		DocumentModel documentModel = getDocumentModel("covadis_di_2018");
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_ok_SIG_DI");

		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_ok_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(31, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
			
			// validation database
			// TODO à reporter dans test DATABASE
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			/*
			ValidatorError error0 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(0);
			ValidatorError error1 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(1);
			ValidatorError error2 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(2);
			*/
			// Assert.assertEquals("La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario 04Fai.", error0.getMessage());
			// Assert.assertEquals("", error1.getMessage());
			// Assert.assertEquals("", error2.getMessage());
//			Assert.assertEquals("SIN_4", error.getFeatureId());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDocumentNotOk() throws Exception {
		DocumentModel documentModel = getDocumentModel("covadis_di_2018");
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_error_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(31, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
			
			// validation database
			// TODO à reporter dans test DATABASE
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			
			ValidatorError error0 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(0);
			ValidatorError error1 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(1);

			Assert.assertEquals("La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario 04Fai.", error0.getMessage());
			Assert.assertEquals("La surface SIN_4 du scénario 01For n'est pas incluse dans le scénario 04Fai.", error1.getMessage());
			// Assert.assertEquals("", error2.getMessage());
//			Assert.assertEquals("SIN_4", error.getFeatureId());
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
