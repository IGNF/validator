package fr.ign.validator.database;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.MultiTableFile;
import fr.ign.validator.data.file.SingleTableFile;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.StaticTable;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.tools.ModelHelper;
import fr.ign.validator.tools.MultiTableReader;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Helper to load document data into a SQL database to validate some constraints
 * (unique, reference, etc.)
 * 
 * @author CBouche
 * @author MBorne
 *
 */
public class Database implements Closeable {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("Database");

    private static final int BATCH_SIZE = 100;

    private static final String ENV_DATABASE_URL = "DB_URL";
    private static final String ENV_DATABASE_USER = "DB_USER";
    private static final String ENV_DATABASE_PASSWORD = "DB_PASSWORD";
    private static final String ENV_DATABASE_SCHEMA = "DB_SCHEMA";

    public static final String POSTGRESQL_DRIVER = "PostgreSQL Native Driver";

    public static final String DEFAULT_SRID = "4326";

    /**
     * Database connection
     */
    private Connection connection;

    /**
     * The SQL schema name (PostGIS only).
     */
    private String schema;

    /**
     * Create or open an SQLITE database
     * 
     * @param sqlitePath
     */
    public Database(File sqlitePath) {
        log.info(MARKER, "Create SQLITE database {}...", sqlitePath);
        try {
            Class.forName("org.sqlite.JDBC");
            String databaseUrl = "jdbc:sqlite:" + sqlitePath.getAbsolutePath();
            connection = DriverManager.getConnection(databaseUrl);
            connection.setAutoCommit(false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            this.connection.close();
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Create or open a PostgreSQL database with a given schema
     * 
     * @param url
     * @param user
     * @param password
     * @param schema
     */
    private Database(String url, String user, String password, String schema) {
        log.info(MARKER, "Create PostgreSQL database {} using schema={}...", url, schema);
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(false);
            this.schema = schema;
            updateCurrentSchema(false);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get SQL connection
     * 
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * True if a schema is defined
     */
    private boolean hasSchema() {
        return !StringUtils.isEmpty(this.schema);
    }

    /**
     * Returns true the database supports geometry type.
     * 
     * Note that it currently assume that postgis is always enabled for postgresql
     * database.
     * 
     * @return
     */
    public boolean hasGeometrySupport() {
        try {
            return connection.getMetaData().getDriverName().equals(Database.POSTGRESQL_DRIVER);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * If defined, ensure that the specified schema exists and set in search_path.
     * 
     * @param reset true if the schema has to be re-created
     * @throws SQLException
     */
    private void updateCurrentSchema(boolean reset) throws SQLException {
        if (!hasSchema()) {
            return;
        }
        // recreate schema is reset is set to true
        if (reset) {
            update("DROP SCHEMA IF EXISTS " + schema + " CASCADE;");
        }
        // ensure that schema exists
        update("CREATE SCHEMA IF NOT EXISTS " + schema + ";");
        // set current search path to use current schema
        update("SET search_path = " + schema + ", public;");
        connection.commit();
    }

    /**
     * Create a validation Database according to environment variables :
     * 
     * <ul>
     * <li>DB_URL (ex : "jdbc:postgresql:validator-test")</li>
     * <li>DB_USER (ex : "postgis")</li>
     * <li>DB_PASSWORD (ex : "postgis")</li>
     * <li>DB_SCHEMA (ex : "validation_{document.id}")</li>
     * </ul>
     * 
     * Note that :
     * 
     * <ul>
     * <li>An SQLITE database is created in validation directory if DB_URL is not
     * specified</li>
     * <li>The default schema for PostgreSQL is "validation" and "public" is
     * forbidden</li>
     * <li>DB_SCHEMA is set a the default schema (search path)
     * <li>
     * </ul>
     *
     * @param document
     * @param reset    true if the schema has to be re-created
     * 
     * @return
     * @throws SQLException
     */
    public static Database createDatabase(Context context, boolean reset) throws SQLException {
        String url = System.getenv(Database.ENV_DATABASE_URL);
        if (StringUtils.isEmpty(url)) {
            return Database.createSqlLiteDatabase(context, reset);
        } else {
            return createPostgresDatabase(context, reset);
        }
    }

    /**
     * Create an SQLITE database as document_database.db in validation directory.
     * 
     * @param context
     * @param reset
     * @return
     * @throws SQLException
     */
    private static Database createSqlLiteDatabase(Context context, boolean reset) throws SQLException {
        // Ensure that validation directory exists
        File validationDirectory = context.getValidationDirectory();
        if (!validationDirectory.exists()) {
            validationDirectory.mkdirs();
        }
        // Open or create sqlite database file in validation directory
        File databasePath = new File(validationDirectory, "document_database.db");
        if (databasePath.exists() && reset) {
            log.info(MARKER, "Remove existing database {}...", databasePath);
            FileUtils.deleteQuietly(databasePath);
        }
        return new Database(databasePath);
    }

    /**
     * Create a PostgreSQL database according to environment variables.
     * 
     * @param context
     * @param reset
     * @return
     * @throws SQLException
     */
    private static Database createPostgresDatabase(Context context, boolean reset) throws SQLException {
        String url = System.getenv(Database.ENV_DATABASE_URL);
        String user = System.getenv(Database.ENV_DATABASE_USER);
        String password = System.getenv(Database.ENV_DATABASE_PASSWORD);
        String schema = System.getenv(Database.ENV_DATABASE_SCHEMA);
        if (StringUtils.isEmpty(schema)) {
            schema = "validation";
        } else if (schema.equals("public")) {
            throw new RuntimeException("The use of DB_SCHEMA=public is forbidden");
        }
        Database database = new Database(url, user, password, schema);
        if (reset) {
            database.updateCurrentSchema(true);
        }
        return database;
    }

    /**
     * Create tables according to the TableModels of the DocumentModel
     * 
     * @throws SQLException
     * @throws IOException
     */
    public void createTables(DocumentModel documentModel) throws SQLException, IOException {
        log.info(MARKER, "Create tables for the DocumentModel '{}' ...", documentModel.getName());
        for (TableModel tableModel : ModelHelper.getTableModels(documentModel)) {
            createTable(tableModel);
        }
        log.info(MARKER, "Create static table from DocumentModel '{}'...", documentModel.getName());
        for (StaticTable staticTable : documentModel.getStaticTables()) {
            createTable(staticTable);
        }
    }

    /**
     * Create a table according to a TableModel
     * 
     * @param tableModel
     * @throws SQLException
     */
    private void createTable(TableModel tableModel) throws SQLException {
        log.info(MARKER, "Create table for {} ...", tableModel);
        String tableName = tableModel.getName();
        FeatureType featureType = tableModel.getFeatureType();
        createTable(tableName, featureType);
    }

    /**
     * Create table for a given FeatureType.
     * 
     * @param tableName
     * @param featureType
     * @throws SQLException
     */
    private void createTable(String tableName, FeatureType featureType) throws SQLException {
        if (featureType.isEmpty()) {
            log.warn(MARKER, "Create table {} skipped (empty FeatureType)", tableName);
            return;
        }
        createTable(tableName, featureType.getAttributeNames());
    }

    /**
     * Create Table for a given StaticTable
     *
     * @param staticTable
     * @throws IOException
     * @throws SQLException
     */
    private void createTable(StaticTable staticTable) throws IOException, SQLException {
        TableReader reader = TableReader.createTableReader(staticTable.getData());
        String[] inputColumns = reader.getHeader();
        createTable(staticTable.getName(), Arrays.asList(inputColumns));
    }

    /**
     * Create a table with a list of text columns.
     * 
     * @param string
     * @param columnNames
     */
    public void createTable(String tableName, List<String> columnNames) throws SQLException {
        log.info(MARKER, "Create table for the TableModel '{}' with text columns...", tableName);

        StringBuilder query = new StringBuilder();
        query.append("CREATE TABLE " + tableName + " (");
        query.append(" __id INTEGER,");
        query.append(" __file TEXT,");
        for (int i = 0; i < columnNames.size(); i++) {
            if (i != 0) {
                query.append(",");
            }
            query.append(columnNames.get(i) + " TEXT");
        }
        query.append(");");

        update(query.toString());
        connection.commit();
    }

    /**
     * Create indexes for all tables on unique fields.
     * 
     * @param document
     * @throws SQLException
     */
    public void createIndexes(DocumentModel documentModel) throws SQLException {
        log.info(MARKER, "Create indexes for {} ...", documentModel);
        for (TableModel tableModel : ModelHelper.getTableModels(documentModel)) {
            createIndexes(tableModel);
        }
    }

    /**
     * Create indexes for a given table.
     * 
     * @param tableModel
     * @throws SQLException
     */
    public void createIndexes(TableModel tableModel) throws SQLException {
        log.info(MARKER, "Create indexes for {} ...", tableModel);
        List<AttributeType<?>> attributes = tableModel.getFeatureType().getAttributes();
        /*
         * create indexes according to constraints
         */
        for (AttributeType<?> attributeType : attributes) {
            // create index for unique values
            if (attributeType.getConstraints().isUnique()) {
                createIndex(tableModel.getName(), attributeType.getName());
            }
            // create index for referenced values
            if (!StringUtils.isEmpty(attributeType.getConstraints().getReference())) {
                String targetTableName = attributeType.getTableReference();
                String targetColumnName = attributeType.getAttributeReference();
                createIndex(targetTableName, targetColumnName);
            }
        }
    }

    /**
     * Create index idx_{tableName}_{columnName} on {tableName}({columnName}
     * 
     * @param tableName
     * @param columnName
     */
    public void createIndex(String tableName, String columnName) throws SQLException {
        log.info(MARKER, "Create index on {}.{} ...", tableName, columnName);
        String indexName = "idx_" + tableName + "_" + columnName;
        String sql = "CREATE INDEX IF NOT EXISTS " + indexName + " ON " + tableName + " (" + columnName + ")";

        update(sql);
        connection.commit();
    }

    /**
     * Load an entire document to the database (insert mode)
     * 
     * @throws IOException
     * @throws SQLException
     */
    public void load(Context context, Document document) throws IOException, SQLException {
        log.info(MARKER, "Loading data from document '{}'...", document.getDocumentPath());
        for (DocumentFile documentFile : document.getDocumentFiles()) {
            if (documentFile instanceof SingleTableFile) {
                load(context, (SingleTableFile) documentFile);
            } else if (documentFile instanceof MultiTableFile) {
                load((MultiTableFile) documentFile);
            }
        }
        log.info(MARKER, "Loading static table from document model '{}'...", document.getDocumentModel().getName());
        for (StaticTable staticTable : document.getDocumentModel().getStaticTables()) {
            load(context, staticTable);
        }
    }

    /**
     * Load a given {@link SingleTableFile} in the database (insert mode)
     * 
     * @param context
     * @param documentFile
     * @throws IOException
     * @throws SQLException
     */
    void load(MultiTableFile documentFile) throws IOException, SQLException {
        MultiTableModel mutliTableModel = documentFile.getFileModel();
        MultiTableReader reader = documentFile.getReader();
        for (String tableName : reader.getTableNames()) {
            if (mutliTableModel.getTableModelByName(tableName) == null) {
                log.warn(
                    MARKER, "Loading {} from {} : Skipped (table not found in {})",
                    tableName,
                    documentFile.getPath(),
                    mutliTableModel
                );
                continue;
            }
            loadFile(tableName, reader.getTablePath(tableName), StandardCharsets.UTF_8);
        }
    }

    /**
     * Load a given {@link SingleTableFile} in the database (insert mode)
     * 
     * @param file
     * @param fileModel
     * @throws IOException
     * @throws SQLException
     */
    void load(Context context, SingleTableFile tableFile) throws IOException, SQLException {
        FileModel fileModel = tableFile.getFileModel();
        TableReader reader = TableReader.createTableReader(tableFile.getPath(), context.getEncoding());
        loadTable(fileModel.getName(), context.relativize(tableFile.getPath()), reader, context.getEncoding());
    }

    /**
     * Load a given {@link StaticTable} in the database (insert mode)
     * 
     * @param context
     * @param staticTable
     * @throws IOException
     * @throws SQLException
     */
    void load(Context context, StaticTable staticTable) throws IOException, SQLException {
        log.info(
            MARKER, "Load table '{}' from stream '{}' (charset={})...",
            staticTable.getName(), staticTable.getDataReference(), StandardCharsets.UTF_8
        );
        TableReader reader = TableReader.createTableReader(staticTable.getData());
        loadTable(staticTable.getName(), staticTable.getDataReference(), reader, StandardCharsets.UTF_8);
    }

    /**
     * Load a given file into an existing table.
     * 
     * @param tableName
     * @param path
     * @param charset
     * @throws IOException
     * @throws SQLException
     */
    void loadFile(String tableName, File path, Charset charset) throws IOException, SQLException {
        log.info(
            MARKER, "Load table '{}' from file '{}' (charset={})...",
            tableName, path.getAbsolutePath(), charset
        );
        TableReader reader = TableReader.createTableReader(path, charset);
        loadTable(tableName, path.getName(), reader, charset);
    }

    /**
     * Load a given TableReader in the database (insert mode)
     * 
     * @param tableName
     * @param sourceFile
     * @param reader
     * @param charset
     * @throws IOException
     * @throws SQLException
     */
    void loadTable(String tableName, String sourceFile, TableReader reader, Charset charset) throws IOException,
        SQLException {

        String[] inputColumns = reader.getHeader();
        String[] outputColumns = getTableSchema(tableName);

        /*
         * Generate insert into template according to columns
         */
        List<String> columnParts = new ArrayList<>();
        columnParts.add("__id");
        columnParts.add("__file");
        List<String> valueParts = new ArrayList<>();
        valueParts.add("?");
        valueParts.add("?");
        List<Integer> inputIndexes = new ArrayList<>();
        for (int i = 0; i < inputColumns.length; i++) {
            String inputColumn = inputColumns[i];
            for (String outputColumn : outputColumns) {
                if (outputColumn.equalsIgnoreCase(inputColumn)) {
                    // inputColumn exists
                    inputIndexes.add(i);
                    columnParts.add(outputColumn.toLowerCase());
                    valueParts.add("?");
                }
            }
        }

        /* no matching ? */
        if (inputIndexes.isEmpty()) {
            log.warn(
                MARKER, "No matching column found in {} for {} with {}",
                tableName, sourceFile, String.join(",", inputColumns)
            );
            return;
        }

        String sql = "INSERT INTO " + tableName + " (" + String.join(", ", columnParts) + ") VALUES ("
            + String.join(", ", valueParts) + ");";

        /* Create prepared statement... */
        log.debug(MARKER, sql);
        PreparedStatement sth = connection.prepareStatement(sql);
        int count = 0;
        try {
            /* Batch insertion */

            while (reader.hasNext()) {
                String[] row = reader.next();
                // insert line number
                sth.setInt(1, count + 1); // NOSONAR
                // insert file path
                sth.setString(2, sourceFile); // NOSONAR
                // insert values
                int parameterIndex = 2;
                for (int i = 0; i < inputIndexes.size(); i++) {
                    Integer index = inputIndexes.get(i);
                    sth.setString(parameterIndex + 1, row[index]);
                    parameterIndex++;
                }
                sth.addBatch();

                if (++count % BATCH_SIZE == 0) {
                    sth.executeBatch();
                    sth.clearBatch();
                }
            }
            sth.executeBatch();
        } finally {
            sth.close();
        }
        connection.commit();

        log.info(
            MARKER,
            "Load table '{}' from '{}' (charset={}) : completed, {} row(s) loaded",
            tableName, sourceFile, charset, count
        );
    }

    /**
     * Perform any SQL request returning results
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    public RowIterator query(String sql) throws SQLException {
        log.debug(MARKER, sql);
        PreparedStatement sth = connection.prepareStatement(sql);
        boolean hasResultSet = sth.execute();
        if (hasResultSet) {
            return new RowIterator(sth.getResultSet());
        }
        return new RowIterator();
    }

    /**
     * Perform any SQL request that doesn't returns results
     * 
     * @param sql
     * @return
     * @throws SQLException
     */
    public void update(String sql) throws SQLException {
        log.debug(MARKER, sql);
        Statement sth = null;
        try {
            sth = connection.createStatement();
            sth.executeUpdate(sql);
        } finally {
            if (sth != null) {
                sth.close();
            }
        }
    }

    /**
     * Return an Array of String giving column names for a giving table
     * 
     * @param tablename
     * @return
     * @throws SQLException
     * @throws IOException
     */
    private String[] getTableSchema(String tablename) throws SQLException, IOException {
        RowIterator query = query("SELECT * FROM " + tablename + " LIMIT 1");
        String[] header = query.getHeader();
        query.close();
        return header;
    }

    /**
     * Return the number of rows for a given table
     * 
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getCount(String tableName) throws SQLException {
        Statement stmt = connection.createStatement();
        try {
            String query = String.format(
                "SELECT count(*) FROM %s ",
                stmt.enquoteIdentifier(tableName, false)
            );
            ResultSet rs = stmt.executeQuery(query);
            return rs.getInt(1);
        } finally {
            stmt.close();
        }
    }

}
