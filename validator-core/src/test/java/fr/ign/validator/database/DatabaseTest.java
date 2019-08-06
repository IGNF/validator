package fr.ign.validator.database;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.xml.XmlModelManager;

public class DatabaseTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

	Context context ;
	
	@Before
	public void setUp(){
		context = new Context();
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testEmptyDatabaseFromFile() {
		try {
			// test database driver
			File path = new File( folder.getRoot(), "document_database.db" );
			Database database = new Database(path);
			RowIterator it = database.query("SELECT 'test' as test");
			Assert.assertTrue(it.hasNext());
			String[] row = it.next();
			Assert.assertEquals(1, row.length );			
			Assert.assertEquals("test", row[0] );
			Assert.assertFalse(it.hasNext());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateInsertSelect() {
		try {
			File path = new File( folder.getRoot(), "document_database.db" );
			Database database = new Database(path);
			database.query("CREATE TABLE TEST(id TEXT, name TEXT);");
			database.query("INSERT INTO TEST(id, name) VALUES ('1', 'name01');");

			RowIterator iterator = database.query("SELECT * FROM TEST;");
			Assert.assertTrue(iterator.hasNext());
			int indexId = iterator.getColumn("id");
			int indexName = iterator.getColumn("name");

			String[] feature = iterator.next();
			Assert.assertEquals("1", feature[indexId]);
			Assert.assertEquals("name01", feature[indexName]);

			iterator.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}


	protected Document getDocument(boolean validate) throws Exception {
		context.setProjection("EPSG:4326");
		context.setReportBuilder(reportBuilder);

		File documentModelPath = ResourceHelper.getResourceFile(getClass(),"/config/cnig_PLU_2014/files.xml") ;
		XmlModelManager modelLoader = new XmlModelManager();
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);

		File documentPath = ResourceHelper.getResourceFile(getClass(),"/database/41003_PLU_20130903");
		File copy = folder.newFolder(documentPath.getName());
		FileUtils.copyDirectory(documentPath, copy);

		Document document = new Document(documentModel, copy);

		File validationDirectory = new File(copy.getParentFile(), "validation");
		context.setValidationDirectory(validationDirectory);

		if (validate) {
			document.validate(context);
		}

		return document;
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testDatabaseFromNonValidatedDocument() {
		try {
			Document document = getDocument(false);
			Database database = Database.createDatabase(document);
			database.load(context,document);

			// no data will be load (because no validation, no csv, no file mapping
			int count = database.getCount("DOC_URBA");
			Assert.assertEquals(0, count);

			int countPrescription = database.getCount("PRESCRIPTION_SURF");
			Assert.assertEquals(0, countPrescription);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void testDatabaseFromValidatedDocument() {
		try {
			Document document = getDocument(true);
			
			Database database = Database.createDatabase(document);
			database.load(context,document);

			// some feature were load
			// 0 DOC_URBA (because no DOC_URBA table
			int count = database.getCount("DOC_URBA");
			Assert.assertEquals(0, count);
			// 19 PRESCRIPTION_SURF_41003
			// 19 or 38 ? TODO figure how many...
			int countPrescription = database.getCount("PRESCRIPTION_SURF");
			Assert.assertEquals(19,countPrescription);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testLoadSimpleFileWithColumnsAandB() throws Exception {
		Database database = new Database(new File(folder.getRoot(),"test.sqlite"));
		
		
		List<String> columnNames = new ArrayList<>();
		columnNames.add("A");
		columnNames.add("B");
		database.createTable("test", columnNames);
		
		File file = ResourceHelper.getResourceFile(getClass(),"/csv/DUMMY.csv");
		database.loadFile("test", file, StandardCharsets.UTF_8);
		
		Assert.assertEquals( 1, database.getCount("test") );
	}

	
	/**
	 * Test insert file
	 */
	@Test
	public void testLoadFileWithNoMatchingColumns() throws Exception {
		Database database = new Database(new File(folder.getRoot(),"test.sqlite"));

		List<String> columnNames = new ArrayList<>();
		columnNames.add("C");
		columnNames.add("D");
		database.createTable("test", columnNames);
		
		File file = ResourceHelper.getResourceFile(getClass(),"/csv/DUMMY.csv");
		database.loadFile("test", file, StandardCharsets.UTF_8);
		
		Assert.assertEquals( 0, database.getCount("test") );
	}
	
}
