package fr.ign.validator.cnig.sup;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.database.Database;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Helper class to manipulate relations between ACTE, SERVITUDE, GENERATEUR and
 * ASSIETTE files.
 * 
 * @warning relies on utf-8 encoded CSV files in data directory.
 * 
 * @see http://cnig.gouv.fr/wp-content/uploads/2014/09/20140930_STANDARD_SUP_V2013.pdf#page=20&zoom=auto,-260,773
 * @see https://github.com/IGNF/validator/issues/176 (except TABLE_GENERATEUR
 *      and TABLE_ASSIETTE, tables are already loaded in validation database)
 *
 * @author MBorne
 *
 */
public class DatabaseSUP {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DatabaseSUP");

    /**
     * SERVITUDE / Servitude in CNIG standard ("idsup","nomsuplitt")
     */
    public static final String TABLE_SERVITUDE = "servitude";

    /**
     * ACTE_SUP / ActeServitude in CNIG standard ("idacte","fichier")
     */
    public static final String TABLE_ACTE = "acte_sup";

    /**
     * SERVITUDE_ACTE_SUP in CNIG standard ("idsup","idacte")
     */
    public static final String TABLE_SERVITUDE_ACTE = "servitude_acte_sup";

    /**
     * GENERATEUR_SUP_(P/L/S) / GenerateurSup in CNIG standard ("idgen","idsup")
     */
    public static final String TABLE_GENERATEUR = "generateur";

    /**
     * ASSIETTE_SUP_(P/L/S) / AssietteSup in CNIG standard ("idass","idgen")
     */
    public static final String TABLE_ASSIETTE = "assiette";

    public static final String COLUMN_IDSUP = "idsup";
    public static final String COLUMN_NOMSUPLITT = "nomsuplitt";

    public static final String COLUMN_IDACTE = "idacte";
    public static final String COLUMN_FICHIER = "fichier";

    public static final String COLUMN_IDGEN = "idgen";

    public static final String COLUMN_IDASS = "idass";

    /**
     * Lightweight model for ActeServitude
     */
    public class ActeServitude {
        public String idacte;
        public String fichier;
    }

    /**
     * Lightweight model for Servitude
     */
    public class Servitude {
        public String idsup;
        public String nomsuplitt;
    }

    /**
     * database connection
     */
    private Database database;

    /**
     * Create DatabaseSUP as an SQLite database.
     * 
     * @param tempDirectory
     * @throws SQLException
     */
    public DatabaseSUP(File tempDirectory) throws SQLException {
        File databasePath = new File(tempDirectory, "jointure_sup.db");
        log.info(MARKER, "Create DatabaseSUP ...");
        this.database = new Database(databasePath);
        createSchema();
    }

    /**
     * Get database connection
     * 
     * @return
     */
    private Connection getConnection() {
        return database.getConnection();
    }

    /**
     * Create SQL schema for DatabaseJointureSUP
     * 
     * @throws SQLException
     */
    private void createSchema() throws SQLException {
        log.info(MARKER, "Create schema ...");
        createTableServitude();
        createTableActe();
        createTableServitudeActe();
        createTableGenerateur();
        createTableAssiete();
    }

    /**
     * Create SQL table "servitude".
     *
     * @throws SQLException
     * 
     * @see {@link #loadTableServitude(File)}
     */
    private void createTableServitude() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDSUP);
        columns.add(COLUMN_NOMSUPLITT);
        database.createTable(TABLE_SERVITUDE, columns);
        database.createIndex(TABLE_SERVITUDE, COLUMN_IDSUP);
    }

    /**
     * Create SQL table "acte_sup".
     *
     * @throws SQLException
     * 
     * @see {@link #loadTableActe(File)}
     */
    private void createTableActe() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDACTE);
        columns.add(COLUMN_FICHIER);
        database.createTable(TABLE_ACTE, columns);
        database.createIndex(TABLE_ACTE, COLUMN_IDACTE);
    }

    /**
     * Create SQL table "servitude_acte_sup".
     * 
     * @throws SQLException
     * 
     * @see {@link #loadTableServitudeActe(File)}
     */
    private void createTableServitudeActe() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDSUP);
        columns.add(COLUMN_IDACTE);
        database.createTable(TABLE_SERVITUDE_ACTE, columns);
        database.createIndex(TABLE_SERVITUDE_ACTE, COLUMN_IDSUP);
    }

    /**
     * Create SQL table "generateur".
     * 
     * @throws SQLException
     * 
     * @see {@link #loadTableGenerateur(File)}
     */
    private void createTableGenerateur() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDGEN);
        columns.add(COLUMN_IDSUP);
        database.createTable(TABLE_GENERATEUR, columns);
        database.createIndex(TABLE_GENERATEUR, COLUMN_IDGEN);
    }

    /**
     * Create SQL table "assiette".
     *
     * @throws SQLException
     * 
     * @see {@link #loadTableAssiette(File)}
     */
    private void createTableAssiete() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDASS);
        columns.add(COLUMN_IDGEN);
        database.createTable(TABLE_ASSIETTE, columns);
        database.createIndex(TABLE_ASSIETTE, COLUMN_IDASS);
    }

    /**
     * Load a CSV file into TABLE_SERVITUDE.
     * 
     * @param servitudeFile
     * @throws Exception
     * 
     * @see {@link #createTableServitude()}
     */
    public void loadTableServitude(File servitudeFile) throws Exception {
        log.info(MARKER, "load table SERVITUDE from {}...", servitudeFile);
        TableReader reader = TableReader.createTableReader(servitudeFile, StandardCharsets.UTF_8);

        int indexIdSup = reader.findColumn(COLUMN_IDSUP);
        if (indexIdSup < 0) {
            throw new IOException("Column IDSUP not found");
        }

        int indexNomSupLitt = reader.findColumn(COLUMN_NOMSUPLITT);
        if (indexNomSupLitt < 0) {
            log.warn(MARKER, "Column NOMSUPLITT not found, using empty values (optional)");
        }

        PreparedStatement sth = getConnection().prepareStatement(
            "INSERT INTO servitude (idsup,nomsuplitt) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idSup = row[indexIdSup];
            String nomSupLitt = indexNomSupLitt < 0 ? "" : row[indexNomSupLitt];

            sth.setString(1, idSup);
            sth.setString(2, nomSupLitt);

            sth.addBatch();
        }
        sth.executeBatch();
        getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_ACTE.
     * 
     * @param acteSupFile
     * @throws Exception
     * 
     * @see {@link #createTableActe()}
     */
    public void loadTableActe(File acteSupFile) throws Exception {
        log.info(MARKER, "load table ACTE_SUP from {}...", acteSupFile);
        TableReader reader = TableReader.createTableReader(acteSupFile, StandardCharsets.UTF_8);

        int indexIdActe = reader.findColumn(COLUMN_IDACTE);
        if (indexIdActe < 0) {
            throw new IOException("Column IDACTE not found");
        }

        int indexFichier = reader.findColumn(COLUMN_FICHIER);
        if (indexFichier < 0) {
            throw new IOException("Colonne FICHIER non trouvée");
        }

        PreparedStatement sth = getConnection().prepareStatement(
            "INSERT INTO acte_sup (idacte,fichier) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idActe = row[indexIdActe];
            String fichier = row[indexFichier];

            sth.setString(1, idActe);
            sth.setString(2, fichier);

            sth.addBatch();
        }
        sth.executeBatch();
        getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_SERVITUDE_ACTE.
     * 
     * @param servitudeActeSupFile
     * @throws Exception
     * 
     * @see {@link #createTableServitudeActe()}
     */
    public void loadTableServitudeActe(File servitudeActeSupFile) throws Exception {
        log.info(MARKER, "load table SERVITUDE_ACTE_SUP from {}...", servitudeActeSupFile);
        TableReader reader = TableReader.createTableReader(servitudeActeSupFile, StandardCharsets.UTF_8);

        int indexIdSup = reader.findColumn(COLUMN_IDSUP);
        if (indexIdSup < 0) {
            throw new IOException("Colonne idSup non trouvée");
        }

        int indexIdActe = reader.findColumn(COLUMN_IDACTE);
        if (indexIdActe < 0) {
            throw new IOException("Colonne idActe non trouvée");
        }

        PreparedStatement sth = getConnection().prepareStatement(
            "INSERT INTO servitude_acte_sup (idsup,idacte) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idSup = row[indexIdSup];
            String idActe = row[indexIdActe];

            sth.setString(1, idSup);
            sth.setString(2, idActe);

            sth.addBatch();
        }
        sth.executeBatch();
        getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_GENERATEUR.
     * 
     * @param generateurSupFile
     * @throws Exception
     * 
     * @see {@link #createTableGenerateur()}
     */
    public void loadTableGenerateur(File generateurSupFile) throws Exception {
        log.info(MARKER, "load table GENERATEUR from {}...", generateurSupFile);
        TableReader reader = TableReader.createTableReader(generateurSupFile, StandardCharsets.UTF_8);

        int indexIdGen = reader.findColumn(COLUMN_IDGEN);
        if (indexIdGen < 0) {
            throw new IOException("Colonne IDGEN non trouvée");
        }

        int indexIdSup = reader.findColumn(COLUMN_IDSUP);
        if (indexIdSup < 0) {
            throw new IOException("Colonne idSup non trouvée");
        }

        PreparedStatement sth = getConnection().prepareStatement(
            "INSERT INTO generateur (idgen,idsup) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idGen = row[indexIdGen];
            String idSup = row[indexIdSup];

            sth.setString(1, idGen);
            sth.setString(2, idSup);

            sth.addBatch();
        }
        sth.executeBatch();
        getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_ASSIETTE.
     * 
     * @param path
     * @throws Exception
     * 
     * @see {@link #createTableAssiete()}
     */
    public void loadTableAssiette(File assietteSupFile) throws Exception {
        log.info(MARKER, "load table GENERATEUR from {}...", assietteSupFile);
        TableReader reader = TableReader.createTableReader(assietteSupFile, StandardCharsets.UTF_8);

        int indexIdAss = reader.findColumn(COLUMN_IDASS);
        if (indexIdAss < 0) {
            throw new IOException("Colonne IDASS non trouvée");
        }

        int indexIdGen = reader.findColumn(COLUMN_IDGEN);
        if (indexIdGen < 0) {
            throw new IOException("Colonne IDGEN non trouvée");
        }

        PreparedStatement sth = getConnection().prepareStatement(
            "INSERT INTO assiette (idass,idgen) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idAss = row[indexIdAss];
            String idGen = row[indexIdGen];

            sth.setString(1, idAss);
            sth.setString(2, idGen);

            sth.addBatch();
        }
        sth.executeBatch();
        getConnection().commit();
    }

    /**
     * Return the number of rows in TABLE_ACTE.
     * 
     * @return
     * @throws SQLException
     */
    public int getCount(String tableName) throws SQLException {
        return database.getCount(tableName);
    }

    /**
     * Find actes for a given idGen
     *
     * @param idGen
     * @return
     */
    public List<ActeServitude> findActesByGenerateur(String idGen) {
        String sql = "SELECT DISTINCT a.* FROM acte_sup a "
            + " LEFT JOIN servitude_acte_sup sa ON a.idacte = sa.idacte "
            + " LEFT JOIN generateur ON generateur.idsup = sa.idsup "
            + " WHERE generateur.idgen = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idGen);
            return fetchActes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find actes for a given idAss.
     * 
     * @param idAss
     * @return
     */
    public List<ActeServitude> findActesByAssiette(String idAss) {
        String sql = "SELECT DISTINCT a.* FROM acte_sup a "
            + " LEFT JOIN servitude_acte_sup sa ON a.idacte = sa.idacte "
            + " LEFT JOIN generateur ON generateur.idsup = sa.idsup "
            + " LEFT JOIN assiette ON assiette.idgen = generateur.idgen "
            + " WHERE assiette.idass = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idAss);
            return fetchActes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch unique Acte from ResultSet.
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<ActeServitude> fetchActes(ResultSet rs) throws SQLException {
        List<ActeServitude> result = new ArrayList<ActeServitude>();
        while (rs.next()) {
            ActeServitude acte = new ActeServitude();
            acte.idacte = rs.getString(COLUMN_IDACTE);
            acte.fichier = rs.getString(COLUMN_FICHIER);
            result.add(acte);
        }
        return result;
    }

    /**
     * Helper to extract "fichier" values
     * 
     * @param actes
     * @return
     */
    public List<String> getFichiers(List<ActeServitude> actes) {
        HashSet<String> result = new HashSet<>(actes.size());
        for (ActeServitude acte : actes) {
            result.add(acte.fichier);
        }
        return new ArrayList<>(result);
    }

    /**
     * Find servitudes for a given idGen
     *
     * @param idGen
     * @return
     */
    public List<Servitude> findServitudesByGenerateur(String idGen) {
        String sql = "SELECT DISTINCT s.* FROM generateur g "
            + " LEFT JOIN servitude s ON s.idsup = g.idsup "
            + " WHERE g.idgen = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idGen);
            return fetchServitudes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find servitudes for a given idAss.
     * 
     * @param idAss
     * @return
     */
    public List<Servitude> findServitudesByAssiette(String idAss) {
        String sql = "SELECT DISTINCT s.* FROM assiette a "
            + " LEFT JOIN generateur g ON a.idgen = g.idgen "
            + " LEFT JOIN servitude s ON s.idsup = g.idsup "
            + " WHERE a.idass = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idAss);
            return fetchServitudes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch unique Acte from ResultSet.
     * 
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<Servitude> fetchServitudes(ResultSet rs) throws SQLException {
        List<Servitude> result = new ArrayList<Servitude>();
        while (rs.next()) {
            Servitude servitude = new Servitude();
            servitude.idsup = rs.getString(COLUMN_IDSUP);
            servitude.nomsuplitt = rs.getString(COLUMN_NOMSUPLITT);
            result.add(servitude);
        }
        return result;
    }

    /**
     * Helper to extract "nomSupLitt" values
     * 
     * @param actes
     * @return
     */
    public List<String> getNomSupLitts(List<Servitude> servitudes) {
        HashSet<String> result = new HashSet<>(servitudes.size());
        for (Servitude servitude : servitudes) {
            result.add(servitude.nomsuplitt);
        }
        return new ArrayList<>(result);
    }

}
