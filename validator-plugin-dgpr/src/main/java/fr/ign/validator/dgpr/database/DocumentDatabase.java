package fr.ign.validator.dgpr.database;

import java.io.File;
import java.io.IOException;
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

import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.exception.InvalidCharsetException;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.CharsetDetector;
import fr.ign.validator.tools.TableReader;

public class DocumentDatabase {

	public static final Logger log    = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentDatabase");

	private Connection connection;

	private Document document;

	public DocumentDatabase(Document document) throws SQLException {
		this.document = document;
		try {
			Class.forName("org.sqlite.JDBC");

			File parentDirectory = document.getDocumentPath().getParentFile();
			File databasePath = new File(parentDirectory, "jointure_sup.db");
			//File databasePath = parentDirectory ;
			String databaseUrl = "jdbc:sqlite:"+ databasePath.getAbsolutePath() ;
			connection = DriverManager.getConnection(databaseUrl);
			connection.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		createSchema();
	}


	/**
	 * Schema creation
	 * 
	 * @throws SQLException
	 */
	private void createSchema() throws SQLException {
		List<FileModel> fileModels = document.getDocumentModel().getFileModels();
		for (FileModel fileModel : fileModels) {
			if (fileModel instanceof TableModel) {
				createTable(fileModel);
			}
		}
	}


	private void createTable(FileModel fileModel) throws SQLException {
		String sql = getCreateTableSql(fileModel);
		// debug SQL
		log.debug(MARKER, sql);
		Statement sth = connection.createStatement();
		sth.executeUpdate(sql);
		connection.commit();
	}


	private String getCreateTableSql(FileModel fileModel) {
		String tablename = fileModel.getName().toLowerCase();
		String sql = "CREATE TABLE " + tablename + " (";

		List<AttributeType<?>> attributes = fileModel.getFeatureType().getAttributes();
		for (AttributeType<?> attribute : attributes) {
			String name = attribute.getName().toLowerCase();
			if (attribute.isIdentifier()) {
				// sql += name + " TEXT PRIMARY KEY" + ", ";
				sql += name + " TEXT " + ", ";
				continue;
			}
			//			if (attribute.isReference()) {
			//				String tableReference = ...;
			//			    String attributeReference = ...;
			//				sql += " " + name + " TEXT REFERENCES " + tableReference + "("+ attributeReference + ")" + ",";
			//				continue;
			//			}
			sql += name + " TEXT" + ", ";
		}

		// remove last coma
		sql = sql.substring(0, sql.length() - 2) + ");";
		return sql;
	}


	public void loadFile(File file, FileModel fileModel) throws IOException, InvalidCharsetException, SQLException {
		// TableReader reader = TableReader.createTableReader(file, CharsetDetector.detectCharset(file)StandardCharsets.UTF_8);
		TableReader reader = TableReader.createTableReader(file, CharsetDetector.detectCharset(file));

		String[] header = reader.getHeader();
		List<AttributeType<?>> attributes = fileModel.getFeatureType().getAttributes();

		// prepare SQL
		String sqlAttPart = "";
		String sqlValuesPart = "";
		List<Integer> indexes = new ArrayList<Integer>();

		for (int i = 0; i < header.length; i++) {
			String att = header[i];
			for (AttributeType<?> attribute : attributes) {
				String name = attribute.getName();
				if (name.toLowerCase().equals(att.toLowerCase())) {
					// l'att du fichier csv existe dans le model de document
					indexes.add(i);
					sqlAttPart += name.toLowerCase() + ", ";
					sqlValuesPart += "?, ";
				}
			}
		}
		sqlAttPart = sqlAttPart.substring(0, sqlAttPart.length() - 2);
		sqlValuesPart = sqlValuesPart.substring(0, sqlValuesPart.length() - 2);
		String sql = "INSERT INTO " + fileModel.getName() + " (" + sqlAttPart + ") VALUES (" + sqlValuesPart + ");";

		PreparedStatement sth = connection.prepareStatement(sql);
		// debug SQL
		log.debug(MARKER, sql);

		while (reader.hasNext()) {
			String[] row = reader.next();
			int parameterIndex = 0;
			for (int i = 0; i < indexes.size(); i++) {
				Integer index = indexes.get(i);
				sth.setString(parameterIndex + 1, row[index]);
				parameterIndex++;
			}
			sth.addBatch();
		}
		sth.executeBatch();
		connection.commit();
	}


	public int getCount(String tableName) throws SQLException{
		PreparedStatement sth = connection.prepareStatement("SELECT count(*) FROM " + tableName) ;
		ResultSet rs = sth.executeQuery() ;
		return rs.getInt(1);
	}


	public RowIterator selectAll(String tablename) throws SQLException {
		return query("SELECT * FROM " + tablename);
	}


	public String[] getSchema(String tablename) throws SQLException, IOException {
		RowIterator query = query("SELECT * FROM " + tablename + " LIMIT 1");
		String[] header = query.getHeader();
		query.close();
		return header;
	}


	public RowIterator query(String sql) throws SQLException {
		PreparedStatement sth = connection.prepareStatement(sql);
		ResultSet rs = sth.executeQuery();
		return new RowIterator(rs);
	}


	public void load() throws IOException, InvalidCharsetException, SQLException {
		List<DocumentFile> files = document.getDocumentFiles();
		for (DocumentFile file : files) {
			loadFile(file.getPath(), file.getFileModel());
		}
	}

}
