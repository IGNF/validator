package fr.ign.validator.database;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
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

	private static final int batchSize = 100;

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
			databasePath.delete();
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
			throw new RuntimeException("The use DB_SCHEMA=public is forbidden");
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
	 */
	public void createTables(DocumentModel documentModel) throws SQLException {
		log.info(MARKER, "Create tables for the DocumentModel '{}' ...", documentModel.getName());
		List<FileModel> fileModels = documentModel.getFileModels();
		for (FileModel fileModel : fileModels) {
			if (fileModel instanceof TableModel) {
				TableModel tableModel = (TableModel) fileModel;
				createTable(tableModel);
			}
		}
	}

	/**
	 * Create a table according to a TableModel
	 * 
	 * @param tableModel
	 * @throws SQLException
	 */
	public void createTable(TableModel tableModel) throws SQLException {
		log.info(MARKER, "Create table for the TableModel '{}' ...", tableModel.getName());
		List<AttributeType<?>> attributes = tableModel.getFeatureType().getAttributes();
		List<String> columns = new ArrayList<String>(attributes.size());

		for (AttributeType<?> attribute : attributes) {
			columns.add(attribute.getName() + " TEXT");
		}

		String sql = "CREATE TABLE " + tableModel.getName() + " (" + String.join(",", columns) + ");";

		update(sql);
		connection.commit();
	}

	/**
	 * Create a table with a list of text columns.
	 * 
	 * @param string
	 * @param columnNames
	 */
	public void createTable(String tableName, List<String> columnNames) throws SQLException {
		log.info(MARKER, "Create table for the TableModel '{}' with text columns...", tableName);
		String sql = "CREATE TABLE " + tableName + " (";
		for (int i = 0; i < columnNames.size(); i++) {
			if (i != 0) {
				sql += ",";
			}
			sql += columnNames.get(i) + " TEXT";
		}
		sql += ");";

		update(sql);
		connection.commit();
	}

	/**
	 * Create indexes for all tables on unique fields
	 * 
	 * @param document
	 * @throws SQLException
	 */
	public void createIndexes(DocumentModel documentModel) throws SQLException {
		for (FileModel file : documentModel.getFileModels()) {
			if (!(file instanceof TableModel)) {
				continue;
			}
			List<AttributeType<?>> attributes = file.getFeatureType().getAttributes();
			/*
			 * create indexes according to constraints
			 */
			for (AttributeType<?> attributeType : attributes) {
				// TODO create index for referenced values
				if (!attributeType.getConstraints().isUnique()) {
					continue;
				}
				createIndex(file.getName(), attributeType.getName());
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
		String indexName = "idx_" + tableName + "_" + columnName;
		String sql = "CREATE INDEX " + indexName + " ON " + tableName + " (" + columnName + ")";

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
		log.info(MARKER, "Load document '{}'...", document.getDocumentPath());
		List<DocumentFile> files = document.getDocumentFiles();
		for (DocumentFile file : files) {
			if (file instanceof TableFile) {
				load(context, file);
			}
		}
	}

	/**
	 * Load a given original file to the database (insert mode)
	 * 
	 * @param file
	 * @param fileModel
	 * @throws IOException
	 * @throws SQLException
	 */
	public void load(Context context, DocumentFile documentFile) throws IOException, SQLException {
		FileModel fileModel = documentFile.getFileModel();
		loadFile(fileModel.getName(), documentFile.getPath(), context.getEncoding());
	}

	/**
	 * Load a given file into an existing table
	 * 
	 * @param tableName
	 * @param path
	 * @param charset
	 * @throws IOException
	 * @throws SQLException
	 */
	public void loadFile(String tableName, File path, Charset charset) throws IOException, SQLException {
		log.info(MARKER, "loadFile({},{},{})...", tableName, path.getAbsolutePath(), charset.toString());
		/*
		 * Create table reader
		 */
		TableReader reader = TableReader.createTableReader(path, charset);

		String[] header = reader.getHeader();
		String[] columnNames = getTableSchema(tableName);

		/*
		 * Generate insert into template according to columns INSERT INTO TABLE (att1,
		 * att2, ...) VALUES (?, ?, ..);
		 */

		List<String> columnParts = new ArrayList<>();
		List<String> valueParts = new ArrayList<>();
		List<Integer> indexes = new ArrayList<Integer>();
		for (int i = 0; i < header.length; i++) {
			String att = header[i];
			for (String columnName : columnNames) {
				if (columnName.toLowerCase().equals(att.toLowerCase())) {
					// l'att du fichier csv existe dans le model de document
					indexes.add(i);
					columnParts.add(columnName.toLowerCase());
					valueParts.add("?");
				}
			}
		}

		/* no matching ? */
		if (indexes.isEmpty()) {
			log.warn(MARKER, "No matching column found in {} for {} with {}", tableName, path,
					String.join(",", header));
			return;
		}

		String sql = "INSERT INTO " + tableName + " (" + String.join(", ", columnParts) + ") VALUES ("
				+ String.join(", ", valueParts) + ");";

		/* Create prepared statement... */
		PreparedStatement sth = connection.prepareStatement(sql);
		log.debug(MARKER, sql);

		/* Batch insertion */
		int count = 0;
		while (reader.hasNext()) {
			String[] row = reader.next();
			int parameterIndex = 0;
			for (int i = 0; i < indexes.size(); i++) {
				Integer index = indexes.get(i);
				sth.setString(parameterIndex + 1, row[index]);
				parameterIndex++;
			}
			sth.addBatch();

			if (++count % batchSize == 0) {
				sth.executeBatch();
				sth.clearBatch();
			}
		}
		sth.executeBatch();
		connection.commit();
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
	private void update(String sql) throws SQLException {
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
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
		PreparedStatement sth = connection.prepareStatement("SELECT count(*) FROM " + tableName);
		ResultSet rs = sth.executeQuery();
		rs.next();
		return rs.getInt(1);
	}

}
