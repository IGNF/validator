package fr.ign.validator.database;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.file.TableFile;
import fr.ign.validator.exception.InvalidCharsetException;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Projection;
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
public class Database {

	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentDatabase");

	private static final int batchSize = 100;
	
	public static final String ENV_DATABASE_URL = "DB_URL";
	public static final String ENV_DATABASE_USER = "DB_USER";
	public static final String ENV_DATABASE_PASSWORD = "DB_PASSWORD";
	public static final String ENV_DATABASE_SCHEMA = "DB_SCHEMA";

	public static final String POSTGRESQL_DRIVER = "PostgreSQL Native Driver";

	public static final String GEOMETRY_COLUMN = "WKT";

	public static final String DEFAULT_SRID = "4326";

	private String schema;
	
	private Projection projection;

	/**
	 * Database connection
	 */
	private Connection connection;

	/**
	 * Create or open an sqlite database
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
	
	public Database(String url, String user, String password, String schema) {
		log.info(MARKER, "Create POSTGRESQL database {}...", url);
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, user, password);
			connection.setAutoCommit(false);
			// save schema
			this.setSchema(schema);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}

	/**
	 * Get database connection
	 * 
	 * @return
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Create database with a schema corresponding to the given document
	 * 
	 * @param document
	 * @return
	 * @throws SQLException
	 */
	public static Database createDatabase(Document document) throws SQLException {
		String url = System.getenv(Database.ENV_DATABASE_URL);
		if (url != null && !url.isEmpty()) {
			return createPostgresDatabase(document);
		}
		// else
		return Database.createSqlLiteDatabase(document);
	}

	
	private static Database createSqlLiteDatabase(Document document) throws SQLException {
		File parentDirectory = document.getDocumentPath().getParentFile();
		File databasePath = new File(parentDirectory, "document_database.db");
		if (databasePath.exists()) {
			log.info(MARKER, "remove existing database {}...", databasePath);
			databasePath.delete();
		}
		Database database = new Database(databasePath);
		if (database.hasSchema()) {
			database.createSchema(database.getSchema());
		}
		database.createTables(document.getDocumentModel());
		return database;
	}
	
	
	private static Database createPostgresDatabase(Document document) throws SQLException {
		String url = System.getenv(Database.ENV_DATABASE_URL);
		String user = System.getenv(Database.ENV_DATABASE_USER);
		String password = System.getenv(Database.ENV_DATABASE_PASSWORD);
		String schema = System.getenv(Database.ENV_DATABASE_SCHEMA);
		Database database = new Database(url, user, password, schema);
		if (database.hasSchema()) {
			database.createSchema(database.getSchema());
		}
		database.createTables(document.getDocumentModel());
		return database;
	}


	public void createSchema(String schemaName) throws SQLException {
		String dropClause = "";
		if (this.isPostgresqlDriver() && !schemaName.equals("public")) {
			dropClause = "DROP SCHEMA IF EXISTS " + schemaName + " CASCADE;";
		}
		String sql = dropClause 
				+ " CREATE SCHEMA IF NOT EXISTS " + schemaName + ";"
				+ " SET search_path = " + schemaName + ", public;";

		// debug SQL
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
		connection.commit();
	}

	
	/**
	 * Create tables according to documentModel
	 * 
	 * @throws SQLException
	 */
	public void createTables(DocumentModel documentModel) throws SQLException {
		List<FileModel> fileModels = documentModel.getFileModels();
		for (FileModel fileModel : fileModels) {
			if (fileModel instanceof TableModel) {
				TableModel tableModel = (TableModel) fileModel;
				createTable(tableModel);
			}
		}
	}

	/**
	 * Create table according to tableModel
	 * 
	 * @param tableModel
	 * @throws SQLException
	 */
	public void createTable(TableModel tableModel) throws SQLException {
		if (this.isPostgresqlDriver()) {
			createPostgisTable(tableModel);
		} else {
			createTable(tableModel.getName(), getColumnNames(tableModel));
		}
	}

	/**
	 * Create table
	 * 
	 * @param string
	 * @param columnNames
	 */
	public void createTable(String tableName, List<String> columnNames) throws SQLException {
		String ifClause = "";
		if (this.isPostgresqlDriver()) {
			ifClause = "IF NOT EXISTS ";
		}
		String sql = "CREATE TABLE " + ifClause + tableName + " (";
		for ( int i = 0; i < columnNames.size(); i++ ){
			if ( i != 0 ){
				sql += ",";
			}
			sql += columnNames.get(i) + " TEXT";
		}
		sql += ");";

		// debug SQL
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
		connection.commit();
	}
	
	/**
	 * Create postgis Table with geometry column
	 * @param tableModel
	 * @throws SQLException 
	 */
	public void createPostgisTable(TableModel tableModel) throws SQLException {
		List<AttributeType<?>> attributes = tableModel.getFeatureType().getAttributes();
		List<String> columns = new ArrayList<String>(attributes.size());

		for (AttributeType<?> attribute : attributes) {
			if (attribute.isGeometry()) {
				columns.add(attribute.getName() + " TEXT");
				columns.add("the_geom geometry(" + attribute.getTypeName() + "," + Database.DEFAULT_SRID + ")");
			} else {
				columns.add(attribute.getName() + " TEXT");
			}
		}

		String sql = "CREATE TABLE IF NOT EXISTS " + tableModel.getName() + " ("
			+ String.join(",", columns)
			+ ");";

		// debug SQL
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
		connection.commit();
		/*
		 * -- Add a spatial index
         * CREATE INDEX mydata_geom_idx ON mydata USING gist (geom);
		 */
	}
	
	/**
	 * Create index idx_{tableName}_{columnName} on {tableName}({columnName}
	 * @param tableName
	 * @param columnName
	 */
	public void createIndex(String tableName, String columnName) throws SQLException {
		String ifClause = "";
		if (this.isPostgresqlDriver()) {
			ifClause = "IF NOT EXISTS ";
		}
		// format : idx_N_prefixTri_CARTE_INOND_S_ddd_ID_CARTE, idx_N_prefixTri_ENJEU_CRISE_P_ddd_ID_SI
		String indexName = "idx_" + tableName + "_" + columnName;
		String sql = "CREATE INDEX " + ifClause + indexName 
				+ " ON " + tableName + " (" + columnName + ")";
		// debug SQL
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
		connection.commit();
	}

	/**
	 * Get column names for a given tableModel
	 * 
	 * @param fileModel
	 * @return
	 */
	private List<String> getColumnNames(TableModel tableModel) {
		List<AttributeType<?>> attributes = tableModel.getFeatureType().getAttributes();
		
		List<String> columnNames = new ArrayList<>(attributes.size());
		for (AttributeType<?> attribute : attributes) {
			columnNames.add(attribute.getName());
		}
		return columnNames;
	}

	/**
	 * Load an entire document to the database (insert mode)
	 * 
	 * @throws IOException
	 * @throws InvalidCharsetException
	 * @throws SQLException
	 */
	public void load(Context context, Document document) throws IOException, InvalidCharsetException, SQLException {
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
	 * @throws InvalidCharsetException
	 * @throws SQLException
	 */
	public void load(Context context, DocumentFile documentFile)
			throws IOException, InvalidCharsetException, SQLException {
		FeatureType featureType = documentFile.getFileModel().getFeatureType();

		loadFile(featureType.getName(), documentFile.getPath(), context.getEncoding());
		if (this.isPostgresqlDriver()) {
			updateGeom(featureType);
		}
	}

	private void updateGeom(FeatureType featureType) throws SQLException {
		// last commit
		if (featureType.isSpatial()) {
			String srid = Database.DEFAULT_SRID;
			if (this.getProjection() != null && this.getProjection().getCode().split(":").length > 1) {
				// must split code
				srid = this.getProjection().getCode().split(":")[1];
			}
			String updateSQL = "UPDATE " + featureType.getName() + " SET the_geom = "
					+ "ST_Multi(ST_Transform(ST_SetSRID(wkt, " + srid + "), 4326));"; 
			log.debug(MARKER, updateSQL);
			Statement sth = connection.createStatement();
			sth.executeUpdate(updateSQL);
			connection.commit();
		}
	}

	/**
	 * Load a given file into an existing table
	 * 
	 * @param tableName
	 * @param path
	 * @param charset
	 * @throws IOException
	 * @throws InvalidCharsetException
	 * @throws SQLException
	 */
	public void loadFile(String tableName, File path, Charset charset)
			throws IOException, InvalidCharsetException, SQLException {
		/*
		 * Create table reader
		 */
		TableReader reader = TableReader.createTableReaderPreferedCharset(path, charset);

		String[] header = reader.getHeader();
		String[] columnNames = getTableSchema(tableName);

		/*
		 * Generate insert into template according to columns INSERT INTO TABLE
		 * (att1, att2, ...) VALUES (?, ?, ..);
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
		if ( indexes.isEmpty() ){
			log.warn(MARKER, "No matching column found in {} for {} with {}", 
				tableName, 
				path,
				String.join(",", header)
			);
			return;
		}
		

		String sql = "INSERT INTO " + tableName 
			+ " (" 
			+ 	String.join(", ", columnParts)
			+ ") VALUES (" 
			+ String.join(", ", valueParts)
			+ ");"
		;

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
	 * Perform any SQL request to DocumentDatabase
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
	 * Return an Array of String giving column names for a giving table
	 * 
	 * @param tablename
	 * @return
	 * @throws SQLException
	 * @throws IOException
	 */
	public String[] getTableSchema(String tablename) throws SQLException, IOException {
		RowIterator query = query("SELECT * FROM " + tablename + " LIMIT 1");
		String[] header = query.getHeader();
		query.close();
		return header;
	}

	/**
	 * Return the number of features of a giving table
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

	/**
	 * Return all the feature of a giving table
	 * 
	 * @param tablename
	 * @return
	 * @throws SQLException
	 */
	public RowIterator selectAll(String tablename) throws SQLException {
		return query("SELECT * FROM " + tablename);
	}
	
	public boolean hasSchema() {
		return this.schema != null && !this.schema.isEmpty();
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public Projection getProjection() {
		return projection;
	}

	public void setProjection(Projection projection) {
		this.projection = projection;
	}
	
	public boolean isPostgresqlDriver() {
		try {
			return connection.getMetaData().getDriverName().equals(Database.POSTGRESQL_DRIVER);
		} catch (SQLException e) {
			return false;
		}
	}

}
