package fr.ign.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
		context.setProjection("EPSG:2154");
		context.setTolerance(1.0);
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
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_SIG_DI");

		Context context = createContext(documentPath);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

			// validation database
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
			Assert.assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testDocumentOkTolerance5() throws Exception {
		DocumentModel documentModel = getDocumentModel("covadis_di_2018");
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_SIG_DI");

		Context context = createContext(documentPath);
		context.setTolerance(5.0);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

			// validation database
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testDocumentNotOkTolerance10() throws Exception {
		DocumentModel documentModel = getDocumentModel("covadis_di_2018");
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

		Context context = createContext(documentPath);
		context.setTolerance(10.0);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_error_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(29, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

			// validation database
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			Assert.assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
			Assert.assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
			ValidatorError error = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).get(0);
			Assert.assertEquals("Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable.", error.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}


	@Test
	public void testDocumentOkWithNoTolerance() throws Exception {
		DocumentModel documentModel = getDocumentModel("covadis_di_2018");
		File documentPath = getSampleDocument("TRI_JTEST_TOPO_SIG_DI");

		Context context = createContext(documentPath);
		context.setTolerance(0.0);
		Document document = new Document(documentModel, documentPath);
		try {
			document.validate(context);
			Assert.assertEquals("TRI_JTEST_TOPO_SIG_DI", document.getDocumentName());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

			// validation database
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
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
			Assert.assertEquals(29, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());
			
			/*
			 * Scénarios d'inclusion : SIN_5 (Moyen) n'est pas incluse dans SIN_6 (Faible)
             * Scénarios d'inclusion : SIN_4 (Fort) n'est pas incluse dans SIN_6 (Faible)
			 */
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
			ValidatorError error0 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(0);
			ValidatorError error1 = report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(1);
			Assert.assertEquals("La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario 04Fai.", error0.getMessage());
			Assert.assertEquals("La surface SIN_4 du scénario 01For n'est pas incluse dans le scénario 04Fai.", error1.getMessage());

			/*
			 * Les ISO_HT couvrant SIN_4 ne couvrent pas l'intégralité des hauteurs d'eau (0-4) (pas d'erreur car un seul objet)
             * Les ISO_HT couvrant SIN_5 ne couvrent pas l'intégralité des hauteurs d'eau (0-2 puis 3-HTMAX)
             */
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED).size());
			/*
			ValidatorError error10 = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED).get(0);
			ValidatorError error11 = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_MIN_MAX_VALUE_UNCOVERED).get(1);
			Assert.assertEquals("Les ISO_HT couvrant SIN_5 ne couvrent pas l'intégralité des hauteurs d'eau de manière unique ([0.00, 2.00] [3.00, null]).", error10.getMessage());
			Assert.assertEquals("Les ISO_HT couvrant SIN_4 ne couvrent pas l'intégralité des hauteurs d'eau de manière unique ([0.00, 4.00]).", error11.getMessage());
			*/

			/*
			 * ZCH_9 et ZCH_10 (scénario Faible) ne constituent pas une partition de SIN_6
			 */
			Assert.assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
// TODO : rendre insensible à l'ordre
//			ValidatorError error20 = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).get(0);
//			Assert.assertEquals("Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6. Leurs périmètres s'intersectent.", error20.getMessage());
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
//			ValidatorError error21 = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).get(0);
//			Assert.assertEquals("Les ISO_DEB ZCD_1, ZCD_2 ne constituent pas une partition de SIN_1 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable.", error21.getMessage());
//			ValidatorError error22 = report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).get(1);
//			Assert.assertEquals("Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable.", error22.getMessage());

			/*
			 * Zone de suralea ZSA_2 non adjacente à l'ouvrage de protection OUV_2
             * Zone soustraite à l'inondation ZSI_2 non adjacente à l'ouvrage de protection 0UV_2
             * Zone inondable SIN_2 non adjacente à l'ouvrage de protection OUV_2
			 */
			// TODO AdjacenceValidator

			/*
			 * Appartenance au même scenario.
			 * L'objet LIC_2 de la classe ISO_COTE_L a pour scénario 02Moy différent de celui
			 * de la surface inondable SIN_4, de scénario 01For, à laquelle il est rattaché.
			 *
			 * L'objet ZCH_7 de la classe ISO_HT_S a pour scénario 01For différent de celui
			 * de la surface inondable SIN_5, de scénario 02Moy, à laquelle il est rattaché.
			 */
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).size());
			ValidatorError error40 = report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).get(0);
			ValidatorError error41 = report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).get(1);
			Assert.assertEquals("L'objet ZCH_7 de la classe N_prefixTri_ISO_HT_suffixIsoHt_S_ddd a un scénario (01For) différent de celui de la surface inondable SIN_5 (02Moy) à laquelle il est rattaché.", error40.getMessage());
			Assert.assertEquals("L'objet LIC_2 de la classe N_prefixTri_ISO_COTE_L_ddd a un scénario (02Moy) différent de celui de la surface inondable SIN_4 (01For) à laquelle il est rattaché.", error41.getMessage());


			/*
			 * Validation unicite et relation
			 */
			Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).size());
			ValidatorError error50 = report.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).get(0);
			ValidatorError error51 = report.getErrorsByCode(DgprErrorCodes.DGPR_IDENTIFIER_UNICITY).get(1);
			Assert.assertEquals("Problème dans la table N_prefixTri_ECOUL_S_ddd : l'identifiant 'ZE_2' est présent 2 fois.", error50.getMessage());
			Assert.assertEquals("Problème dans la table N_prefixTri_ENJEU_CRISE_L_ddd : l'identifiant 'SIEXT' est présent 2 fois.", error51.getMessage());

			Assert.assertEquals(15, report.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).size());
			ValidatorError error52 = report.getErrorsByCode(DgprErrorCodes.DGPR_RELATION_ERROR).get(0);
			Assert.assertEquals("L'objet CSI_1 de la table N_prefixTri_CARTE_INOND_S_ddd doit faire référence à un objet de la table N_prefixTri_TRI_S_ddd via l'attribut ID_TRI. L'attribut n'est pas renseigné (TRI_ZOB) ou alors la relation n'est pas vérifiée."
					, error52.getMessage());

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
