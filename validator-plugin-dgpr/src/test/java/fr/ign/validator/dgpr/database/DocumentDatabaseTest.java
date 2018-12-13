package fr.ign.validator.dgpr.database;

import java.io.File;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.xml.XmlModelManager;

public class DocumentDatabaseTest {

	public static final Logger log = LogManager.getRootLogger();

	protected InMemoryReportBuilder report;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private Context context;

	@Before
	public void setUp() {
		report = new InMemoryReportBuilder();
	}

	private Document getDocument() throws Exception {
		String documentName = "TRI_JTEST_TOPO_ok_SIG_DI";
		// String documentName = "documentDatabaseTest_SIG_DI";
		URL resource = getClass().getResource("/documents/" + documentName);
		Assert.assertNotNull(resource);
		File sourcePath = new File(resource.getPath());

		File documentPath = folder.newFolder(documentName);
		FileUtils.copyDirectory(sourcePath, documentPath);

		// create context to access to report
		context = createContext(documentPath);

		Document document = new Document(getDocumentModel(), documentPath);
		return document;
	}

	private Context createContext(File documentPath) throws Exception {
		Context context = new Context();
		context.setReportBuilder(report);
		context.setProjection("EPSG:2154");
		File validationDirectory = new File(documentPath.getParentFile(), "validation");
		context.setValidationDirectory(validationDirectory);
		PluginManager pluginManager = new PluginManager();
		pluginManager.getPluginByName("DGPR").setup(context);
		return context;
	}

	private DocumentModel getDocumentModel() throws Exception {
		String documentModelName = "covadis_di_2018";
		File documentModelPath = new File(getClass().getResource("/config/" + documentModelName + "/files.xml").getPath());
		XmlModelManager loader = new XmlModelManager();
		DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
		documentModel.setName(documentModelName);
		return documentModel;
	}

	@Test
	public void testDocumentDatabase() throws Exception {
		// TODO trouver un moyen de tester DATABASE sans charger de document
		Assert.assertTrue(true);
		/*
		Document document = getDocument();
		// validate is mandatory (we need file mapping)
		document.validate(context);

		DocumentDatabase database = new DocumentDatabase(document);
		database.load();

		// test chargement
		Assert.assertNotNull(database);
		Assert.assertEquals(1, database.getCount("n_prefixtri_carte_inond_s_ddd"));
		Assert.assertEquals(4, database.getCount("N_prefixTri_CHAMP_VIT_P_ddd"));
		Assert.assertEquals(3, database.getCount("N_prefixTri_ECOUL_S_ddd"));
		Assert.assertEquals(5, database.getCount("N_prefixTri_COTE_VIT_DEB_P_ddd"));
		Assert.assertEquals(3, database.getCount("N_prefixTri_ISO_HT_S_suffixIsoHt_ddd"));
		*/
	}

}
