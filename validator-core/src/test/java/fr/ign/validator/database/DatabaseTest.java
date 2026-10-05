package fr.ign.validator.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
import fr.ign.validator.model.Projection;
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
     * query() without results returns an empty RowIterator (statement already
     * closed, close() was failing with a NullPointerException)
     *
     * @throws Exception
     */
    @Test
    public void testQueryWithoutResults() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "sample.db"));
        RowIterator it = database.query("CREATE TABLE TEST(id TEXT);");
        assertFalse(it.hasNext());
        it.close();
        database.close();
    }

    /**
     * runInSavepoint rolls back the changes of a failing task and the following
     * requests are performed
     *
     * @throws Exception
     */
    @Test
    public void testRunInSavepointRollback() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "sample.db"));
        database.update("CREATE TABLE TEST(id TEXT);");
        database.update("INSERT INTO TEST(id) VALUES ('1');");

        Assert.assertThrows(SQLException.class, () -> {
            database.runInSavepoint(() -> {
                database.update("INSERT INTO TEST(id) VALUES ('2');");
                database.update("UPDATE NOT_FOUND SET test='meuh'");
            });
        });
        assertEquals(1, database.getCount("TEST"));

        database.runInSavepoint(() -> {
            database.update("INSERT INTO TEST(id) VALUES ('3');");
        });
        assertEquals(2, database.getCount("TEST"));
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
    public void testHasGeometrySupportRequiresPostgisExtension() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "sample.db"));

        DatabaseMetaData metadata = (DatabaseMetaData) Proxy.newProxyInstance(
            DatabaseMetaData.class.getClassLoader(),
            new Class<?>[] { DatabaseMetaData.class },
            (proxy, method, args) -> {
                if ("getURL".equals(method.getName())) {
                    return "jdbc:postgresql://example.com:5432/validator";
                }
                return null;
            }
        );

        ResultSet emptyResultSet = (ResultSet) Proxy.newProxyInstance(
            ResultSet.class.getClassLoader(),
            new Class<?>[] { ResultSet.class },
            (proxy, method, args) -> {
                if ("next".equals(method.getName())) {
                    return false;
                }
                return null;
            }
        );

        Statement statement = (Statement) Proxy.newProxyInstance(
            Statement.class.getClassLoader(),
            new Class<?>[] { Statement.class },
            (proxy, method, args) -> {
                if ("executeQuery".equals(method.getName())) {
                    return emptyResultSet;
                }
                return null;
            }
        );

        Connection pgConnection = (Connection) Proxy.newProxyInstance(
            Connection.class.getClassLoader(),
            new Class<?>[] { Connection.class },
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "getMetaData":
                        return metadata;
                    case "createStatement":
                        return statement;
                    case "close":
                        return null;
                    default:
                        return null;
                }
            }
        );

        java.lang.reflect.Field connectionField = Database.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(database, pgConnection);

        assertFalse(database.hasGeometrySupport());
        database.close();
    }

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
     * A row with missing values is loaded with null values (was failing with
     * ArrayIndexOutOfBoundsException)
     *
     * @throws Exception
     */
    @Test
    public void testLoadFileWithShortRow() throws Exception {
        Database database = new Database(new File(folder.getRoot(), "test.sqlite"));
        database.createTable("test", Arrays.asList("A", "B"));

        File file = folder.newFile("short.csv");
        FileUtils.writeStringToFile(file, "A,B\r\na1,b1\r\na2\r\n", StandardCharsets.UTF_8);
        database.loadFile("test", file, StandardCharsets.UTF_8);

        Assert.assertEquals(2, database.getCount("test"));
        try (RowIterator it = database.query("SELECT a, b FROM test WHERE a = 'a2'")) {
            String[] row = it.next();
            Assert.assertEquals("a2", row[0]);
            Assert.assertNull(row[1]);
        }
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
        context.setProjection(Projection.CODE_CRS84);
        File validationDirectory = new File(folder.getRoot(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);
        context.setReportBuilder(reportBuilder);
        return context;
    }

}
