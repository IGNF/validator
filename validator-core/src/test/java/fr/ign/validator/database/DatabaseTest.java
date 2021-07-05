package fr.ign.validator.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;

public class DatabaseTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    /**
     * Create an SQLITE database with a specific path and performs basic tests
     * 
     * @throws Exception
     */
    @Test
    public void testCreateDatabaseFile() throws Exception {
        File databaseFile = new File(folder.getRoot(), "sample.db");
        Database database = new Database(databaseFile);

        // ensure that file is created
        assertTrue(databaseFile.exists());

        // ensure that simple select works without any table
        RowIterator it = database.query("SELECT 'test' as test");
        assertTrue(it.hasNext());
        String[] row = it.next();
        assertEquals(1, row.length);
        assertEquals("test", row[0]);
        assertFalse(it.hasNext());

        it.close();
        database.close();
    }

    @Test(expected = SQLException.class)
    public void testUpdateFail() throws SQLException, IOException {
        File databaseFile = new File(folder.getRoot(), "sample.db");
        Database database = new Database(databaseFile);
        database.update("UPDATE NOT_FOUND SET test='meuh'");
        database.close();
    }

    /**
     * Performs basic test with some queries
     * 
     * @throws Exception
     */
    @Test
    public void testCreateInsertSelect() throws Exception {
        File databaseFile = new File(folder.getRoot(), "sample.db");
        Database database = new Database(databaseFile);
        database.query("CREATE TABLE TEST(id TEXT, name TEXT);");
        database.query("INSERT INTO TEST(id, name) VALUES ('1', 'name01');");

        RowIterator iterator = database.query("SELECT * FROM TEST;");
        assertTrue(iterator.hasNext());
        int indexId = iterator.getColumn("id");
        int indexName = iterator.getColumn("name");

        String[] feature = iterator.next();
        assertEquals("1", feature[indexId]);
        assertEquals("name01", feature[indexName]);

        iterator.close();
        database.close();
    }

    /**
     * Load DUMMY.csv file with A,B,WKT columns in table with A,B columns.
     * 
     * @throws Exception
     */
    @Test
    public void testLoadSimpleFileWithColumnsAandB() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "test.sqlite"));

        List<String> columnNames = new ArrayList<>();
        columnNames.add("A");
        columnNames.add("B");
        database.createTable("test", columnNames);

        File file = ResourceHelper.getResourceFile(getClass(), "/csv/DUMMY.csv");
        database.loadFile("test", file, StandardCharsets.UTF_8);

        Assert.assertEquals(1, database.getCount("test"));

        database.close();
    }

    /**
     * Load DUMMY.csv file with A,B,WKT columns in table with C,D columns.
     * 
     * @throws Exception
     */
    @Test
    public void testLoadFileWithNoMatchingColumns() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "test.sqlite"));

        List<String> columnNames = new ArrayList<>();
        columnNames.add("C");
        columnNames.add("D");
        database.createTable("test", columnNames);

        File file = ResourceHelper.getResourceFile(getClass(), "/csv/DUMMY.csv");
        database.loadFile("test", file, StandardCharsets.UTF_8);

        Assert.assertEquals(0, database.getCount("test"));

        database.close();
    }

    @Test
    public void testLoadAdresseMultiple() throws Exception {
        Context context = createTestContext();

        Document document = getSampleDocument("adresse", "adresse-multiple");
        Database database = Database.createDatabase(context, true);
        database.createTables(document.getDocumentModel());
        /*
         * Note that this line is required to find mapping between DocumentFiles and
         * FileModels
         */
        document.findDocumentFiles(context);
        database.load(context, document);
        assertEquals(8, database.getCount("adresse"));

        database.close();
    }

    /**
     * Get sample Document
     * 
     * @return
     * @throws IOException
     */
    protected Document getSampleDocument(String documentModelName, String documentName) throws IOException {
        File documentModelPath = ResourceHelper.getResourceFile(
            getClass(), "/config-json/" + documentModelName + "/files.json"
        );
        ModelReader modelLoader = new JsonModelReader();
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);

        File documentPath = ResourceHelper.getResourceFile(getClass(), "/documents/" + documentName);
        File copy = folder.newFolder(documentPath.getName());
        FileUtils.copyDirectory(documentPath, copy);
        return new Document(documentModel, copy);
    }

    /**
     * Create a test context
     * 
     * @return
     */
    private Context createTestContext() {
        InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();
        Context context = new Context();
        context.setProjection("EPSG:4326");
        File validationDirectory = new File(folder.getRoot(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);
        context.setReportBuilder(reportBuilder);
        return context;
    }

}
