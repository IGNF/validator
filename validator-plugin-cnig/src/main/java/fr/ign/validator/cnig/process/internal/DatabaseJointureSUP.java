package fr.ign.validator.cnig.process.internal;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ign.validator.database.Database;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Helper class to manipulate relations between ACTE, SERVITUDE, GENERATEUR and ASSIETTE files
 * 
 * TODO : rebase table insertion on Database.loadTable (requires renaming table columns)
 * 
 * @warning works with utf-8 encoded csv files (validation directory) 
 * 
 * @author MBorne
 *
 */
public class DatabaseJointureSUP {

	public static final String TABLE_ACTE       = "acte" ;
	public static final String TABLE_SERVITUDE  = "servitude" ;
	public static final String TABLE_GENERATEUR = "generateur" ;
	public static final String TABLE_ASSIETTE   = "assiette" ;

	/**
	 * database connection
	 */
	private Database database;
	

	/**
	 * Construction of database with path
	 * 
	 * @param databaseDirectory
	 * @throws SQLException
	 */
	public DatabaseJointureSUP(File databaseDirectory) throws SQLException {
		File databasePath = new File(databaseDirectory, "jointure_sup.db");
		this.database = new Database(databasePath);
		createSchema();
	}

	/**
	 * Get database connection
	 * @return
	 */
	private Connection getConnection(){
		return database.getConnection();
	}
	
	/**
	 * Schema creation
	 * 
	 * @throws SQLException
	 */
	private void createSchema() throws SQLException {
		createTableActe();
		createTableServitude();
		createTableGenerateur();
		createTableAssiete();
	}

	private void createTableActe() throws SQLException {
		List<String> columns = new ArrayList<>();
		columns.add("id");
		columns.add("fichier");
		database.createTable(TABLE_ACTE, columns);
		database.createIndex(TABLE_ACTE, "id");
	}

	private void createTableServitude() throws SQLException {
		List<String> columns = new ArrayList<>();
		columns.add("id");
		columns.add("id_acte");
		database.createTable(TABLE_SERVITUDE, columns);
		database.createIndex(TABLE_SERVITUDE, "id");
	}

	private void createTableGenerateur() throws SQLException {
		List<String> columns = new ArrayList<>();
		columns.add("id");
		columns.add("id_servitude");
		database.createTable(TABLE_GENERATEUR, columns);
		database.createIndex(TABLE_GENERATEUR, "id");
	}

	private void createTableAssiete() throws SQLException {
		List<String> columns = new ArrayList<>();
		columns.add("id");
		columns.add("id_generateur");
		database.createTable(TABLE_ASSIETTE, columns);
		database.createIndex(TABLE_ASSIETTE, "id");
	}
	
	
	/**
	 * Return the number of "actes"
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int getCountActes() throws SQLException {
		return database.getCount(TABLE_ACTE);
	}
	
	/**
	 * Loading "actes" file
	 * 
	 * @param path
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void loadFileActe(File actesFile) throws Exception {
		TableReader reader = TableReader.createTableReader(actesFile,StandardCharsets.UTF_8);
		
		int indexIdActe  = reader.findColumn("IDACTE");
		if ( indexIdActe < 0 ){
			throw new IOException("Colonne IDACTE non trouvée");
		}
		
		int indexFichier = reader.findColumn("FICHIER");
		if ( indexFichier < 0 ){
			throw new IOException("Colonne FICHIER non trouvée");
		}
		
		PreparedStatement sth = getConnection().prepareStatement("INSERT INTO acte (id,fichier) VALUES (?,?)" );
		
		while ( reader.hasNext() ){
			String[] row = reader.next() ;
			
			String idActe  = row[indexIdActe] ;
			String fichier = row[indexFichier] ;
			
			sth.setString(1, idActe);
			sth.setString(2, fichier);
			
			sth.addBatch();
		}
		sth.executeBatch();		
		getConnection().commit();
	}
	
	
	/**
	 * Return the number of "servitudes"
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int getCountServitude() throws SQLException {
		return database.getCount(TABLE_SERVITUDE);
	}
	

	/**
	 * Loading "servitudes" file
	 * 
	 * @param path
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void loadFileServitude(File servitudesFile) throws Exception {
		TableReader reader = TableReader.createTableReader(servitudesFile,StandardCharsets.UTF_8);
		
		int indexIdSup  = reader.findColumn("idSup");
		if ( indexIdSup < 0 ){
			throw new IOException("Colonne IDACTE non trouvée");
		}
		
		int indexIdActe = reader.findColumn("idActe");
		if ( indexIdActe < 0 ){
			throw new IOException("Colonne FICHIER non trouvée");
		}
		
		
		PreparedStatement sth = getConnection().prepareStatement("INSERT INTO servitude (id,id_acte) VALUES (?,?)" );
		
		while ( reader.hasNext() ){
			String[] row = reader.next() ;
			
			String idSup  = row[indexIdSup] ;
			String idActe = row[indexIdActe] ;
			
			sth.setString(1, idSup);
			sth.setString(2, idActe);
			
			sth.addBatch();
		}
		sth.executeBatch();
		getConnection().commit();
	}
	
	/**
	 * Return the number of "générateurs"
	 * 
	 * @return
	 * @throws SQLException
	 */
	public int getCountGenerateur() throws SQLException {
		return database.getCount(TABLE_GENERATEUR);
	}


	/**
	 * Loading "générateurs" file
	 * 
	 * @param path
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void loadFileGenerateur(File generateursFile) throws Exception {
		TableReader reader = TableReader.createTableReader(generateursFile,StandardCharsets.UTF_8);
		
		int indexIdGen  = reader.findColumn("idGen");
		if ( indexIdGen < 0 ){
			throw new IOException("Colonne IDGEN non trouvée");
		}
		
		int indexIdSup = reader.findColumn("idSup");
		if ( indexIdSup < 0 ){
			throw new IOException("Colonne FICHIER non trouvée");
		}
		
		
		PreparedStatement sth = getConnection().prepareStatement("INSERT INTO generateur (id,id_servitude) VALUES (?,?)" );
		
		while ( reader.hasNext() ){
			String[] row = reader.next() ;
			
			String idGen = row[indexIdGen] ;
			String idSup = row[indexIdSup] ;
			
			sth.setString(1, idGen);
			sth.setString(2, idSup);
			
			sth.addBatch();
		}
		sth.executeBatch();
		getConnection().commit();
	}
	

	/**
	 * Loading "assiettes" file
	 * 
	 * @param path
	 * @throws IOException 
	 * @throws SQLException 
	 */
	public void loadFileAssiette(File assiettesFile) throws Exception {
		TableReader reader = TableReader.createTableReader(assiettesFile,StandardCharsets.UTF_8);
		
		int indexIdAss  = reader.findColumn("IDASS");
		if ( indexIdAss < 0 ){
			throw new IOException("Colonne IDASS non trouvée");
		}
		
		int indexIdGen = reader.findColumn("IDGEN");
		if ( indexIdGen < 0 ){
			throw new IOException("Colonne IDGEN non trouvée");
		}
		
		PreparedStatement sth = getConnection().prepareStatement("INSERT INTO assiette (id,id_generateur) VALUES (?,?)" );
		
		while ( reader.hasNext() ){
			String[] row = reader.next() ;
			
			String idAss = row[indexIdAss] ;
			String idGen = row[indexIdGen] ;
			
			sth.setString(1, idAss);
			sth.setString(2, idGen);
			
			sth.addBatch();
		}
		sth.executeBatch();
		getConnection().commit();
	}

	/**
	 * Return the number of "assiettes
	 * "
	 * @return
	 * @throws SQLException 
	 */
	public Object getCountAssiette() throws SQLException {
		return database.getCount(TABLE_ASSIETTE);
	}

	
	/**
	 * Finding "actes" for a "générateur"
	 * 
	 * @param idGen
	 * @return 
	 * @throws SQLException
	 */
	public List<String> findFichiersByGenerateur(String idGen) {
		String sql = "SELECT * FROM acte "
				+ " LEFT JOIN servitude ON acte.id = servitude.id_acte "
				+ " LEFT JOIN generateur ON generateur.id_servitude = servitude.id "
				+ " WHERE generateur.id = ?"
		;
		try {
			PreparedStatement sth = getConnection().prepareStatement(sql);
			sth.setString(1, idGen);
			return getFichiersFromResultSet(sth.executeQuery()) ;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Finding "actes" for a "assiette"
	 * 
	 * @param idGen
	 * @return 
	 * @throws SQLException
	 */
	public List<String> findFichiersByAssiette(String idAss) {
		String sql = "SELECT * FROM acte "
				+ " LEFT JOIN servitude ON acte.id = servitude.id_acte "
				+ " LEFT JOIN generateur ON generateur.id_servitude = servitude.id "
				+ " LEFT JOIN assiette ON assiette.id_generateur = generateur.id "
				+ " WHERE assiette.id = ?"
		;
		try {
			PreparedStatement sth = getConnection().prepareStatement(sql);
			sth.setString(1, idAss);
			return getFichiersFromResultSet(sth.executeQuery()) ;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException 
	 */
	private List<String> getFichiersFromResultSet(ResultSet rs) throws SQLException{
		List<String> result = new ArrayList<String>() ;
		while (rs.next()) {
			result.add( rs.getString("fichier") );
        }
		return result ;
	}

	
	
}
